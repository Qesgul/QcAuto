package main;

import Provider.ConfigFileProvider;
import Provider.IssueTypeId;
import Util.JiraUtil;
import Util.ServerConnectsUtil;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ServerQcAuto {
    private static List<String> fileList = new ArrayList<>();
    private static List<String> logFileList = new ArrayList<>();
    private static final String BUG_PROJECT_KEY = "QSC";

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        getLogFilePath(ConfigFileProvider.instance().getString("enviroment"), getExceptionList());
        ServerConnectsUtil.getLogFiles(logFileList);
        JiraUtil.createBug(ConfigFileProvider.instance().getIssueInputBuilder(BUG_PROJECT_KEY, IssueTypeId.BUG_TYPE_ID.id), fileList);
    }

    private static List<String> getExceptionList() {
        List<String> list = new ArrayList<>();
        list.add("account");
        list.add("content");
        list.add("degreeweb");
        list.add("peixunweb");
        list.add("qingshuapi");
        list.add("similarity");
        return list;
    }

    private static void getLogFilePath(String branch, List<String> items) {
        fileList.clear();
        logFileList.clear();
        String localPath = ConfigFileProvider.instance().getFullPath();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);
        items.forEach(item -> {
            logFileList.add(ServerConnectsUtil.serverBasePath + branch + "/" + item + "/" + item + ".txt-" + dateStr + "-statistics.txt");
            fileList.add(localPath + item + ".txt-" + dateStr + "-statistics.txt");
        });
    }
}
