package main;

import Provider.ConfigFileProvider;
import Provider.IssueTypeId;
import Util.JiraUtil;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class QcAuto {
    private static List<String> fileList = new ArrayList<>();
    private static final String BUG_PROJECT_KEY = "QSC";

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        getDir(ConfigFileProvider.instance().getFullPath());
        JiraUtil.createBug(ConfigFileProvider.instance().getIssueInputBuilder(BUG_PROJECT_KEY, IssueTypeId.BUG_TYPE_ID.id), fileList);
    }

    public static void getDir(String path) {
        File file = new File(path);
        if (!file.exists()) {//判断是否待删除目录是否存在
            System.err.println("The dir are not exists!");
        }
        File[] fs =file.listFiles();
        for (File files:fs){
            fileList.add(files.toString());
        }
    }
}
