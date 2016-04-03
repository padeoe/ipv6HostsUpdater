package service;

import java.io.IOException;

/**
 * Created by padeoe on 2016/4/3.
 */
public class UploadTask {
    private String serverAddr = "padeoe.com";
    private String serverPath = "/usr/share/tomcat/webapps/ROOT/";
    private String user = "username";
    private String pwd = "password";
    private String port = "port";
    private String localPath="./hosts";

    public static void main(String[] args) {
        new Start().updateHosts();
        new UploadTask().uploadHosts();
    }

    public void uploadHosts() {
        System.out.println("start uploading hosts to "+serverAddr);
        String[] args = new String[]{"pscp", "-P", port, "-pw",pwd,localPath,user+"@"+serverAddr+":"+serverPath};
        pscp(args);
    }

    public static void pscp(String[] args) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process=runtime.exec(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}
