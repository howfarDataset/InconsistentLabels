This document is to be maintained and is currently only available in Chinese.  
ͨ�����ֵ�MA-SZZ����ȱ�����ݼ����貽�裺  
0.ѡȡ5��ͨ��github��jira�������Ŀ����������������Щ��Ŀ��GitHub�����ڰ汾�ķ���������ͬ��������������߰汾�ķ������ڷ������ڵͰ汾�������Ұ汾����Ҫ���ڵ���10��  
   ("zeppelin","shiro","maven","flume","mahout")  
1.SZZApplication_v1.java��JIRA��ȡ���ⱨ��projectName.csv  
   java���棬JQL��ѯ����  
   java�ļ���SZZApplication_v1.java��JiraRetriever.java  
   ����·����mySZZ\issue_reports_from_JIRA\issue_reports\projectName\projectName.csv  
2.get_git_log_from_github_project_local.ps1��github��clone�ı��زֿ��ȡgit_log.csv  
   powershell�ű�, git log����:git log --date=iso --name-only --since='2003-1-1' --pretty=format:'%H#SEP#%ad#SEP#%cd#SEP#%s'  
   ����·����mySZZ\git_log_from_GitRepository\projectName\git_log.csv  
3.main_maching_bugFixingCommits_sha.py����projectName.csv��git_log.csvƥ��bug-fixing��commitsha��projectName_bug_commit_all.txt  
   python�ű�������ƥ��  
   ����·����mySZZ\matching_bugid_fixingsha\projectName_bug_commit_all.txt  
   ���ʾ����FLUME-3328 6f33de9bfca7f6d4a30043c0387f2c534dac7440 2019-04-04 15:35:00 2019-05-03 11:49:04�����ⱨ�洴��ʱ�����޸�ʱ�䣩  
4.git_show.ps1���bug-fixing��commitsha����Ӧ���޸���ϢprojectName_commitsha.txt  
   powershell�ű�, git show����:git show  
   ����·����mySZZ\git_show_bugFixingCommitsID\projectName\projectName_commitsha.txt  
5.main_getBuggyLineNum_from_git_show_txt.py���bug-fixing��commitsha����Ч�ı��޸���java�ļ�������Ч�ı��޸ĵ��е��кš����˷�֧�ϲ����ǿɱ���Ĵ���͸�ʽ���ġ�buggyFileAndLineNum_projectName_commitsha.txt  
   python�ű����������  
   ����·����mySZZ\buggyFileAndLineNum_from_git_show\projectName\buggyFileAndLineNum_projectName_commitsha.txt  
   ���ʾ����56,56 0ab026e07b7c852454dd2b9a281f81249cf3d52f^ zeppelin-interpreter/src/main/java/org/apache/zeppelin/interpreter/util/InterpreterOutputStream.java  
6.git_blame_l.ps1��bug-fixing��commitsha��ǰһ���ύ(^)�����bug-introducing��commitsha���ļ��������ߣ�����ʱ�䣬����bug�Ĵ����С�projectName_commitsha_pre.txt  
   powershell�ű�, git blame����:git blame -l -f -L $blameline $commitsha_pre $filedir  
   ����·����mySZZ\git_blame_l_from_buggyFileAndLineNum\projectName\projectName_commitsha_pre.txt  
   ���ʾ����31ecbbf79f6b9f2483a48bcdd6e81d0ff7ae594c src/java/com/cloudera/flume/master/FlumeMaster.java (Andrew Bayer 2011-08-02 16:03:58 +0000 152)     ConfigurationManager base = new ConfigManager(cfgStore);
7.main_find_bugIntroducingTime.py���ÿ������buggy line���ύ�ź�����ʱ�䣬�޸��ύ�ź��޸�ʱ�䣬���ⱨ��Ĵ���ʱ�䡣����������ʱ����ڴ���ʱ��������ύ��buggyLinesIntervals.csv  
   python�ű�������ƥ��  
   ����·����mySZZ\bugIntroducingTime_and_bugFixingtime\projectName\buggyLinesIntervals.csv  
8.main_generateUdb.py����understand���ߴ�Դ��������ÿ���汾��udb�ļ�  
   python�ű���understand��������е�����Ҫ�滻��: und create -db (udbPath_saved) -languages java add (sourceCodePath) analyze -all  
   ����·����mySZZ\GetMetrics\udb\projectName\projectName-version.udb  
9.main_getMetrics_from_udb.pyΪÿ��udb�ļ�ͨ��python����perl�ű��Զ���ȡ����  
   python�ű�main_getMetrics_from_udb.py��pythonCallperl.py��perl�ű�qm_java.pl  
   ����·����mySZZ\GetMetrics\metrics\projectName\projectName-version.csv  
10.main_merge.py�����ɵĶ����ļ��ϲ��ڲ��ࡣ��Ϊȡ������ʱ������ڲ����Ӱ�죬�����ɶ����ͬ�ļ����������������Ҫ���ˡ�  
   python�ű�  
   ����·����mySZZ\GetMetrics\metrics_mergeInnerClass\projectName\projectName-version.csv  
11.��github�򱾵�git�ֿ��ȡ����汾�ķ�������releaseDate.txt  
   ����·����mySZZ\MappingBugsToVersions\releaseDate\projectName\releaseDate.txt  
   ���ʾ����0.5.0,2015-07-11  
12.labeling_v2.R���ݰ汾�ķ�������releaseDate.txt��bug������ʱ����޸�ʱ��buggyLinesIntervals.csv��Ϊÿ���汾�Ķ����ļ�projectName-version.csv����bug��ǩ��projectName-version.csv  
   R�ű�  
   ����·����mySZZ\MappingBugsToVersions\bugDataSet\projectName\projectName-version.csv  