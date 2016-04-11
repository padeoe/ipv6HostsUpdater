package service;

import java.util.*;

/**
 * Created by padeoe on 2016/3/18.
 */
public class IPTest {
    static String DNSServer = "2001:4860:4860::8888";
    static int port = 443;
    static int timeout = 800;
    static int threadNumber = 5;
    static int n = 0;
    static int fixed = 0;
    static int reDNS=0;
    static int problem = 0;
    static int deleted=0;

    public static List<HostsItem> testAllIP() {
        HostsReader hostsReader = new HostsReader("C:\\Windows\\System32\\drivers\\etc\\hosts");
        HostsMap hostsMap = hostsReader.getHostsMap();
        Map<String, String> domainMap = hostsReader.getDomainMap();
        ArrayList<HostsItem> hostsItems = hostsReader.getHostsItemArrayList();

        String[] ipArray = hostsMap.IPArray();

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
                                IP currentIP = new IP(testip);
                                if (!currentIP.findNearIP(port, timeout)) {
                                    ArrayList<String> hostNameList = hostsMap.getHostName(testip);
                                    for (String domain : hostNameList) {
                                        HostsItem hostsItem = new HostsItem(null, domain);
                                        switch (hostsItem.reDNS(DNSServer, port, timeout)) {
                                            case -1:
                                                domainMap.remove(domain);
                                                System.out.println("Non-existent domain: " + domain);
                                                deleted++;
                                                break;
                                            case 0:
                                                System.out.println("fail: " + domain);
                                                problem++;
                                                break;
                                            case 1:
                                                domainMap.put(domain, hostsItem.getIp());
                                                reDNS++;
                                                fixed++;
                                                break;
                                        }
                                    }
                                } else {
                                    fixed+=hostsMap.getHostName(testip).size();
                                    for (String hostname : hostsMap.getHostName(testip)) {
                                        domainMap.put(hostname, currentIP.toString());
                                    }

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
        System.out.println("All threads complete! " + fixed + " updated,"+reDNS+" reDNS," + problem + " fail,"+deleted+" deleted");

        hostsItems.forEach(hostsItem -> hostsItem.setIp(domainMap.get(hostsItem.getDomain())));
        new HostsModify("hosts").writeHostsFile(hostsItems);
        return hostsItems;
    }

    public static List<HostsItem> reDNSAllIP(){
        HostsReader hostsReader = new HostsReader("C:\\Windows\\System32\\drivers\\etc\\hosts");
        hostsReader.getHostsItemArrayList();
        ArrayList<HostsItem> hostsItems = hostsReader.getHostsItemArrayList();
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
                            String testip = currentHostItem.getIp();
                           // if(!IP.isReachable(testip,443,800)){
                                currentHostItem.reDNS(getDNSServer(),getPort(),getTimeout());
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
