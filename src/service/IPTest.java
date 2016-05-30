package service;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by padeoe on 2016/3/18.
 */
public class IPTest {
    static String DNSServer = "2001:4860:4860::8888";
    static int port = 443;
    static int timeout = 800;
    static int threadNumber = 30;
    static int n = 0;
    static int fixed = 0;
    static int reDNS=0;
    static int problem = 0;
    static int deleted=0;
    Date a=new Date();

    public static List<HostsItem> testAllIP() {
        NewHostReader hostsReader = new NewHostReader("C:\\Windows\\System32\\drivers\\etc\\hosts");
        Map<String,List<HostsItem>>ipMap=hostsReader.getIpMap();
        Map<String,HostsItem>domainMap=hostsReader.getDomainMap();
        List<HostsItem>hostsItems=hostsReader.getHostsItemArrayList();
        String[]ipArray=ipMap.keySet().toArray(new String[ipMap.size()]);

        ArrayList<Thread> threadArrayList = new ArrayList<>();
        final int threadnumber = threadNumber;
        for (int i = 0; i < threadnumber; i++) {
            final int a = i;
            threadArrayList.add(new Thread() {
                @Override
                public void run() {
                    int id = ipArray.length - a;
                    for (int j = 1; id > 0; j++) {
                        try {
                            String testip = ipArray[id - 1];
                            if (!IP.isReachable(testip, port, timeout)) {
                           //     System.out.println(testip);
                                IP currentIP = new IP(testip);
                                if (!currentIP.findNearIP(port, timeout)) {
                                    List<HostsItem> hostNameList = ipMap.get(testip);
                                    for (HostsItem hostsItem : hostNameList) {
                                        switch (hostsItem.reDNS(DNSServer, port, timeout)) {
                                            case -1:
                                                domainMap.get(hostsItem.getDomain()).setIp(null);
                                                System.out.println("Non-existent domain: " + hostsItem.getDomain());
                                                deleted++;
                                                break;
                                            case 0:
                                                System.out.println("fail: " + hostsItem.getDomain());
                                                problem++;
                                                break;
                                            case 1:
                                                domainMap.put(hostsItem.getDomain(),hostsItem);
                                                reDNS++;
                                                fixed++;
                                                break;
                                        }
                                    }
                                } else {
                                    ipMap.get(testip).parallelStream().forEach(hostsItem -> domainMap.put(hostsItem.getDomain(),hostsItem));
                                    fixed+=ipMap.get(testip).size();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        n++;
                        id = ipArray.length - threadnumber * j - a;
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
        System.out.println("All threads complete! " + fixed + " updated,"+reDNS+" reDNS," + problem + " fail,"+deleted+" deleted");
        hostsItems.forEach(hostsItem -> hostsItem.setIp(domainMap.get(hostsItem.getDomain()).getIp()));
        new HostsModify("hosts").writeHostsFile(hostsItems);
        return hostsItems;
    }

    public static List<HostsItem> testAllIP2() {
        HostsReader hostsReader = new HostsReader("C:\\Windows\\System32\\drivers\\etc\\hosts");
        HostsMap hostsMap = hostsReader.getHostsMap();
        Map<String, String> domainMap = hostsReader.getDomainMap();
        ArrayList<HostsItem> hostsItems = hostsReader.getHostsItemArrayList();
        Set<Map.Entry<String, ArrayList<String>>> hostMapSet=hostsMap.entrySet();
        Stream<Map.Entry<String, ArrayList<String>>>ipStream=hostMapSet.parallelStream();
        ipStream.filter(hostMap->!IP.isReachable(hostMap.getKey(), port, timeout)).forEach(ip->System.out.println(ip.getKey()));
        return null;
    }

    public static List<HostsItem> reDNSAllIP(){
        HostsReader hostsReader = new HostsReader("C:\\Windows\\System32\\drivers\\etc\\hosts");
        ArrayList<HostsItem> hostsItems = hostsReader.getHostsItemArrayList();
        System.out.println(hostsItems.size());
        ArrayList<Thread> threadArrayList = new ArrayList<>();
        final int threadnumber = threadNumber;
        for (int i = 0; i < threadnumber; i++) {
            final int a = i;
            threadArrayList.add(new Thread() {
                @Override
                public void run() {
                    int id = hostsItems.size() - a;
                    for (int j = 1; id > 0; j++) {
                        try {
                            HostsItem currentHostItem=hostsItems.get(id - 1);
                           // if(!IP.isReachable(testip,443,800)){
                                switch (currentHostItem.reDNS(getDNSServer(),getPort(),getTimeout())){
                                    case -1:
                                        System.out.println("不存在"+currentHostItem.getDomain()+" "+(id-1));
                                        break;
                                    case 0:
                                        System.out.println("失败"+currentHostItem.getDomain()+" "+(id-1));
                                        break;
                                    case 1:
                                        System.out.println("成功"+currentHostItem.getDomain()+"=>"+(id-1));
                                        break;
                                };
                          //  }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        n++;
                        id = hostsItems.size() - threadnumber * j - a;
                    }

                }
            });
        }

        for (Thread thread : threadArrayList) {
            thread.start();
        }
        for (Thread thread : threadArrayList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        new HostsModify("hosts").writeHostsFile(hostsItems);
        return hostsItems;
    }

    public static String getDNSServer() {
        return DNSServer;
    }

    public static void setDNSServer(String DNSServer) {
        IPTest.DNSServer = DNSServer;
        System.out.println("dns server set as "+DNSServer);
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        IPTest.port = port;
    }

    public static int getTimeout() {
        return timeout;
    }

    public static void setTimeout(int timeout) {
        IPTest.timeout = timeout;
    }

    public static int getThreadNumber() {
        return threadNumber;
    }

    public static void setThreadNumber(int threadNumber) {
        IPTest.threadNumber = threadNumber;
    }
}
