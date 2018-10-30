package service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by padeoe on 2016/4/22.
 */
public class NewHostReader {
    private Map<String,List<HostsItem>>ipMap;
    private Map<String,HostsItem>domainMap=new HashMap<>();
    private List<HostsItem> hostsItemList;
    private String hostsPath;
    public NewHostReader(String hostsPath){
        this.hostsPath = hostsPath;
        init();
    }

    public static void main(String[] args) {
        new NewHostReader("hosts");
    }

    private void init(){
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader(hostsPath));
            hostsItemList=bufferedReader.lines().parallel().map(line->getHostItem(line)).filter(line->line!=null).collect(Collectors.toList());
            ipMap=hostsItemList.parallelStream().collect(Collectors.groupingBy(HostsItem::getIp));
            hostsItemList.forEach(hostsItem -> domainMap.put(hostsItem.getDomain(),hostsItem));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Map<String,List<HostsItem>>getIpMap(){
        return ipMap;
    }
    public List<HostsItem>getHostsItemArrayList(){
        return hostsItemList;
    }
    public Map<String,HostsItem>getDomainMap(){
        return domainMap;
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
}
