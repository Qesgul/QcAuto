package Util;

import Provider.ConfigFileProvider;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import java.io.*;
import java.util.List;

public class ServerConnectsUtil {
    private static String hostname = "192.168.1.15";
    private static int port = 22;
    private static String username = "root";
    private static String password = "qyff2011";
    public static String serverBasePath = "/var/log/dairlylog/";

    public static void getLogFiles(List<String> filePaths) {
        deleteDir(getLocalPath());
        File folder = new File(getLocalPath());
        if (!folder.exists()) {
            folder.mkdir();
        }
        for (String filePath : filePaths) {
            downloadLogFiles(filePath);
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

    public static void downloadLogFiles(String filePath) {
        Connection conn = new Connection(hostname, port);
        Session ssh = null;
        try {
            //连接到主机
            conn.connect();
            //使用用户名和密码校验
            boolean isconn = conn.authenticateWithPassword(username, password);
            if (!isconn) {
                System.out.println("用户名称或者是密码不正确");
            } else {
                System.out.println("已经连接OK");
                SCPClient clt = conn.createSCPClient();
                ssh = conn.openSession();
                ssh.execCommand("find " + filePath);
                InputStream is = new StreamGobbler(ssh.getStdout());
                BufferedReader brs = new BufferedReader(new InputStreamReader(is));
                while (true) {
                    String line = brs.readLine();
                    if (line == null) {
                        break;
                    }
                    clt.get(line, getLocalPath());
                }
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
