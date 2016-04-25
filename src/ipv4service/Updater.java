package ipv4service;

import service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by padeoe on 2016/4/3.
 */
public class Updater {
    static String[]ipBlock=new String[]{"216.58.199.","66.102.1.","64.233.162.","173.252.120."};

    public static void main(String[] args) {
        String hostsPath="D:\\My Program\\hosts Editor\\router\\hosts";
        HostsReader hostsReader=new HostsReader(hostsPath);
        String currentIP=hostsReader.getCurrentGoogleIP();
        Map<String,String>domainMap= hostsReader.getDomainMap();
        List<HostsItem>hostsItems=hostsReader.getHostsItemArrayList();
        HostsMap hostsMap=hostsReader.getHostsMap();

        if(!new IP(currentIP).isAvailableGoogleSearchIP(600)){
            String newip=testAllIP(600,16);
            if(newip!=null){
                hostsMap.getHostName(currentIP).forEach(domain->domainMap.put(domain,newip));
                hostsItems.forEach(hostsItem -> hostsItem.setIp(domainMap.get(hostsItem.getDomain())));
                new HostMoidfy_v4(hostsPath).writeHostsFile(hostsItems);
                UploadTask uploadTask=new UploadTask();
                uploadTask.setLocalPath(hostsPath);
                uploadTask.setServerPath("/usr/share/tomcat/webapps/ROOT/liantonghosts");
                uploadTask.uploadHosts();
            }
        }
        else{
            System.out.println("hosts no need to update");
        }
    }
    public static String testAllIP(int timeout,int threadNumber){
        for(int i=0;i<ipBlock.length;i++){
            List<String> ipList=testIPBlock(ipBlock[i],timeout,threadNumber);
            if(ipList.size()>0){
                return ipList.get(0);
            }
        }
        return null;
    }
    /**
     * 测试IP段
     *
     * @param ipSegment 十进制点分式IP地址的前三段以'.'结尾
     * @param timeout   超时时间，用于过略超时时间内可达的IP地址
     * @return 可用的IP地址集合
     */
    public static List<String> testIPBlock(String ipSegment, int timeout,int threadNumber) {
        List<String> IPSet = new ArrayList<>();
        ArrayList<Thread> threadArrayList = new ArrayList<>();
        final int threadnumber = threadNumber;
        for (int i = 0; i < threadnumber; i++) {
            final int a = i;
            threadArrayList.add(new Thread() {
                @Override
                public void run() {
                    int id = 256 - a;
                    for (int j = 1; id > 0; j++) {
                        try {
                            String ip = ipSegment + (id - 1);
                            if (new IP(ip).isAvailableGoogleSearchIP(timeout)) {
                                IPSet.add(ip);
                                System.out.println(ip);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        id = 256 - threadnumber * j - a;
                    }
                }
            });
        }
        threadArrayList.forEach(thread -> thread.start());
        threadArrayList.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        return IPSet;
    }
}
