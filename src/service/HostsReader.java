package service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to read the "hosts" file
 * Created by padeoe on 2016/3/18.
 */
public class HostsReader {
    private String hostsPath;

    /**
     *
     * @param hostsPath "hosts" file path,usually it's
     *                  "C:\Windows\System32\drivers\etc\hosts" in Windows,"/etc/hosts"in Linux,
     *                  of course you can use the path of a hosts path just for testing
     */
    public HostsReader(String hostsPath) {
        this.hostsPath = hostsPath;
    }

    /**
     * read the hosts file into {@link HostsItem} list,it will drop comments and invalid items
     * @return contains all valid hostitem from the hosts file in {@code hostPath}
     */
    public List<HostsItem> getHostsContent(){
        ArrayList<HostsItem> hostsItemArrayList=new ArrayList<>();
        File hostsFile=new File(hostsPath);
        FileReader fileReader= null;
        try {
            fileReader = new FileReader(hostsFile);
            BufferedReader bufferedReader=new BufferedReader(fileReader);
            String line=null;
            HostsItem hostsItem_currentline=null;
            try {
                while((line=bufferedReader.readLine())!=null){
                    hostsItem_currentline=getHostItem(line);
                    if(hostsItem_currentline!=null){
                        hostsItemArrayList.add(hostsItem_currentline);
                    }
                }
                return hostsItemArrayList;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * analyse the line in hosts file by dropping comments,split ip and hostsname
     * and construct into an {@linkplain HostsItem} Object
     * @param hostsLine a single line in hosts file
     * @return
     */
    private static HostsItem getHostItem(String hostsLine){
        hostsLine=hostsLine.trim();
        int indexOfComment=hostsLine.indexOf("#");
        //just comments
        if(indexOfComment==0){
            return null;
        }
        //contains comments
        if(indexOfComment!=-1){
            hostsLine=hostsLine.substring(0,indexOfComment);
        }
        String item[]=hostsLine.split(" ");
        if(item.length!=2) {
           // System.out.println("不合法的行"+hostsLine);
            return null;
        }
        return new HostsItem(item[0],item[1]);
    }

    public String getHostsPath() {
        return hostsPath;
    }
}
