package Util.Util;

import Provider.ConfigFileProvider;
import Provider.ServerLog;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import java.io.*;
import java.util.List;

public class ServerConnectsUtil {
    private static ConfigFileProvider configFileProvider = ConfigFileProvider.instance();


    public static void getLogFiles(List<ServerLog> serverLogList) {
        deleteDir(getLocalPath());
        File folder = new File(getLocalPath());
        if (!folder.exists()) {
            folder.mkdir();
        }
        for (ServerLog serverLog : serverLogList) {
            downloadLogFiles(serverLog);
        }
    }

    public static String getLocalPath() {
        return ConfigFileProvider.instance().getFullPath();
    }

    public static boolean deleteDir(String path) {
        File file = new File(path);
        if (!file.exists()) {//判断是否待删除目录是否存在
            System.err.println("The dir are not exists!");
            return false;
        }
        String[] content = file.list();//取得当前目录下所有文件和文件夹
        for (String name : content) {
            File temp = new File(path, name);
            if (!temp.delete()) {//直接删除文件
                System.err.println("Failed to delete " + name);
            }
        }
        return true;
    }

    public static void downloadLogFiles(ServerLog serverLog) {
        Connection conn = new Connection(configFileProvider.getString("hostname"), Integer.valueOf(configFileProvider.getString("port")));
        Session ssh = null;
        try {
            //连接到主机
            conn.connect();
            //使用用户名和密码校验
            boolean isconn = conn.authenticateWithPassword(configFileProvider.getString("serveruser"), configFileProvider.getString("serverpwd"));
            if (!isconn) {
                System.out.println("用户名称或者是密码不正确");
            } else {
                SCPClient clt = conn.createSCPClient();
                ssh = conn.openSession();
                ssh.execCommand("find " + serverLog.getLogFilePath());
                InputStream is = new StreamGobbler(ssh.getStdout());
                BufferedReader brs = new BufferedReader(new InputStreamReader(is));
                String line = brs.readLine();
                if (line != null) {
                    System.out.println("已经连接OK，正在下载文件");
                    clt.get(line, getLocalPath());
                }else System.out.println("未找到相关文件");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            //连接的Session和Connection对象都需要关闭
            if (ssh != null) {
                ssh.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }


}
