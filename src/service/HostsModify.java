package service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * This class is used to modify the hosts file
 * Created by padeoe on 2016/3/18.
 */
public class HostsModify {
    private String hostsPath;

    public HostsModify(String hostsPath) {
        this.hostsPath = hostsPath;
    }

    public void writeHostsFile(List<HostsItem> hostsList){
        StringBuilder hostBuilder=new StringBuilder();
        for(HostsItem hostsItem:hostsList){
            hostBuilder.append(hostsItem.getIp());
            hostBuilder.append(' ');
            hostBuilder.append(hostsItem.getDomain());
            hostBuilder.append(System.getProperty("line.separator"));
        }
        String hostsContent=hostBuilder.toString();
        writeHostsFile(hostsPath,hostsContent);
    }

    public static void writeHostsFile(String hostsPath, String hostsContent){
        try {
            FileWriter fileWriter=new FileWriter(new File(hostsPath));
            fileWriter.write(hostsContent);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
