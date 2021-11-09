package com.SZZ.jiraAnalyser.git;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JiraRetriever {
	private String jiraURL;
	private String savedPath;
	private String projectName;
	private URL url;
	private URLConnection connection;
	private Document d;
	private PrintWriter pw;

	/**
	 * Class for retrieving all Jira issues. The retrieval must be done only if
	 * the csv not yet present it. Otherwise it must be just updated.
	 * 
	 * @param jiraURL
	 * @param projectName
	 */
	public JiraRetriever(String jiraURL, String projectName) {
		this.jiraURL = jiraURL;
		this.projectName = projectName;
		try {
			pw = new PrintWriter(new FileOutputStream(new File(projectName + "-log.txt"),
					true /* append = true */));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Class for retrieving all Jira issues. The retrieval must be done only if
	 * the csv not yet present it. Otherwise it must be just updated.
	 * 
	 * @param jiraURL
	 * @param projectName
	 */
	public JiraRetriever(String jiraURL, String savedPath, String projectName) {
		this.jiraURL = jiraURL;
		this.projectName = projectName;
		this.savedPath = savedPath;
		try {
			File file=new File(savedPath);
			if (!file.exists()  && !file.isDirectory())
			{
				file.mkdirs();
			}
			pw = new PrintWriter(new FileOutputStream(new File(savedPath + projectName + "-log.txt"),
					true /* append = true */));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * It gets a the XML Document from the stream
	 * 
	 * @param stream
	 * @return
	 */
	private Document parseXML(InputStream stream) {
		DocumentBuilderFactory objDocumentBuilderFactory = null;
		DocumentBuilder objDocumentBuilder = null;
		Document doc = null;
		try {
			objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
			objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();
			doc = objDocumentBuilder.parse(stream);
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return doc;
	}

	private int getTotalNumberIssues() {
		String tempQuery = "?jqlQuery=project+%3D+{0}+ORDER+BY+key+DESC&tempMax=1";
		tempQuery = tempQuery.replace("{0}", projectName);
		try {
			url = new URL(jiraURL + tempQuery);
			connection = url.openConnection();
			d = parseXML(connection.getInputStream());
			NodeList descNodes = d.getElementsByTagName("item");
			Node node = descNodes.item(0);
			for (int p = 0; p < node.getChildNodes().getLength(); p++) {
				if (node.getChildNodes().item(p).getNodeName().equals("key")) {
					String key = (node.getChildNodes().item(p).getTextContent());
					key = key.replaceFirst(".*?(\\d+).*", "$1");
					return Integer.parseInt(key);
				}
			}
		} catch (Exception e) {
			pw.println(e.getMessage());
		}
		return 0;
	}

	public void printIssues() {
		int page = 0;
		int totalePages = (int) Math.ceil(((double) getTotalNumberIssues() / 1000));
		String fileName = projectName + "_" + page + ".csv";
		File file = new File(savedPath + fileName);
		while (file.exists()) {
			page++;
			fileName = projectName + "_" + page + ".csv";
			file = new File(savedPath + fileName);
		}
		if (page > 0) {
			page--;
			fileName = projectName + "_" + page + ".csv";
			file = new File(savedPath + fileName);
			file.delete();
		}

		while (true) {
			String tempQuery = "?jqlQuery=project+%3D+{0}+ORDER+BY+key+ASC&tempMax=1000&pager/start={1}";
			tempQuery = tempQuery.replace("{0}", projectName);
			tempQuery = tempQuery.replace("{1}", ((page) * 1000) + "");
			if (totalePages >= (page + 1))
				System.out.println("Download Jira issues. Page: " + (page + 1) + "/" + totalePages);
			try {
				url = new URL(jiraURL + tempQuery);
				connection = url.openConnection();
				d = parseXML(connection.getInputStream());

				NodeList descNodes = d.getElementsByTagName("item");
				if (descNodes.getLength() == 0)
					return;
				fileName = savedPath + projectName + "_" + page + ".csv";
				file = new File(fileName);
				if (file.exists() && !file.isDirectory()) {
					return;
				}
				PrintWriter pw = null;
				try {
					pw = new PrintWriter(file);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				printHeader(pw);
				printIssuesOfPage(d, pw);
				pw.close();
				page++;
			} catch (Exception e) {
				e.printStackTrace();
				pw.println(("Retrying in 1 minute"));
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				printIssues();
			}
		}
	}

	private void printHeader(PrintWriter pw) {
//		String header = "issueKey;title;resolution;status;assignee;createdDateEpoch;resolvedDateEpoch;type;attachments;priority;comments;";
		String header = "issueKey,type,resolution,status,createdDateEpoch,resolvedDateEpoch";
		pw.println(header);
	}

	/**
	 * 
	 * @param doc
	 * @param nodeName
	 * @return
	 */
	private void printIssuesOfPage(Document doc, PrintWriter pw) {
		NodeList descNodes = doc.getElementsByTagName("item");
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
		for (int i = 0; i < descNodes.getLength(); i++) {
			Node node = descNodes.item(i);
			String issueKey = "";
//			String priority = "";
//			String title = "";
			String resolution = "";
			String status = "";
//			String assignee = "";
//			String commentsString = "";
			String type = "";
//			long createdDateEpoch = 0;//时间戳，不方便
//			long resolvedDateEpoch = 0;//时间戳，不方便
			Date temp_createdDate = new Date();
			Date temp_resolvedDate = new Date();
	        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//创建DateFormat的对象，在构造器中传入跟要转换格式的String字符串
			String str_createdDate = "";
			String str_resolvedDate = "";
			NodeList children = node.getChildNodes();
//			List<String> attachmentsList = new LinkedList<String>();
//			List<String> commentsList = new LinkedList<String>();
			for (int p = 0; p < children.getLength(); p++) {
				switch (children.item(p).getNodeName()) {
//				case "title":
//					title = children.item(p).getTextContent().replace(";", "");
//					break;
				case "resolution":
					resolution = children.item(p).getTextContent();
					break;
				case "key":
					issueKey = children.item(p).getTextContent();
					break;
				case "created":
					String createdDate = children.item(p).getTextContent();
					try {
//						createdDateEpoch = sdf.parse(createdDate).getTime();
						temp_createdDate = sdf.parse(createdDate);
						str_createdDate = dateFormat.format(temp_createdDate);
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				case "resolved":
					String resolveddDate = children.item(p).getTextContent();
					try {
//						resolvedDateEpoch = sdf.parse(resolveddDate).getTime();
						temp_resolvedDate = sdf.parse(resolveddDate);
						str_resolvedDate = dateFormat.format(temp_resolvedDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case "status":
					status = children.item(p).getTextContent();
					break;
//				case "priority":
//					priority = children.item(p).getTextContent();
//					break;
//				case "assignee":
//					assignee = children.item(p).getTextContent();
//					break;
//				case "comments":
//					NodeList comments = children.item(p).getChildNodes();
//					for (int u = 0; u < comments.getLength(); u++) {
//						commentsList.add(children.item(p).getTextContent());
//					}
//					break;
//				case "attachments":
//					NodeList attachments = children.item(p).getChildNodes();
//					// System.out.println(attachments.getLength());
//					for (int u = 0; u < attachments.getLength(); u++) {
//						Node attachment = attachments.item(u);
//						NamedNodeMap attchmentName = attachment.getAttributes();
//						if (attchmentName != null) {
//							String att = attchmentName.getNamedItem("name").getNodeValue();
//							attachmentsList.add(att);
//						}
//					}
//					break;
				case "type":
					type = children.item(p).getTextContent();
					break;
				}
			}
			String toPrint = issueKey + "," + type + "," + resolution + ","  + status + "," + str_createdDate + "," + str_resolvedDate;
			
//			String toPrint = issueKey + ";" + title + ";" + resolution + ";" + status + ";" + assignee + ";"
//					+ createdDateEpoch + ";" + resolvedDateEpoch + ";" + type + ";" + attachmentsList.toString() + ";"
//					+ priority + ";";
//			for (String comment : commentsList) {
//				toPrint += comment.replace(";", "").replace(":", "").replace(".", "").replace(",", "").replace("\n", "")
//						.replace("\r", "").replace("\t", "") + ";";
//			}
			
			// pw是一个高级流,包装了字节流、字符流和字符缓冲流。应该不需要再写缓冲。
			pw.println(toPrint);

		}

	}
}