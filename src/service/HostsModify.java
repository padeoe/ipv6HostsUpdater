package service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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


    public void writeHostsFile(List<HostsItem>hostsItems){
        StringBuilder hostBuilder=new StringBuilder();
        hostBuilder.append(getTitle());
        for(HostsItem hostsItem:hostsItems){
            if(hostsItem.getIp()!=null){
                hostBuilder.append(hostsItem.getIp());
                hostBuilder.append(' ');
                hostBuilder.append(hostsItem.getDomain());
                hostBuilder.append(System.getProperty("line.separator"));
            }
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

    public String getTitle(){
        StringBuilder hostBuilder=new StringBuilder();
        hostBuilder.append("#+=======================================================+");
        hostBuilder.append(System.getProperty("line.separator"));
        hostBuilder.append("#+                Author: padeoe@gmail.com               +");
        hostBuilder.append(System.getProperty("line.separator"));
        hostBuilder.append("#+  Project: https://github.com/padeoe/ipv6HostsUpdater  +");
        hostBuilder.append(System.getProperty("line.separator"));
        hostBuilder.append("#+            Update @ http://padeoe.com/hosts           +");
        hostBuilder.append(System.getProperty("line.separator"));
        hostBuilder.append("#+             updated at:");
        hostBuilder.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
        hostBuilder.append("            +");
        hostBuilder.append(System.getProperty("line.separator"));
        hostBuilder.append("#+=======================================================+");
        hostBuilder.append(System.getProperty("line.separator"));
        return hostBuilder.toString();
    }

}
