package service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by padeoe on 2016/3/18.
 */
public class IPTest {
    static String DNSServer="2001:4860:4860::8888";
    static int port=443;
    static int timeout=800;
    /** the thread number should not be too large,or the server will deny the connection! */
    static int threadNumber=16;
    static int n = 0;
    static int fixed=0;
    static int problem=0;

    public static void testAllIP(){
        HostsReader hostsReader=new HostsReader("C:\\Windows\\System32\\drivers\\etc\\hosts");
        List<HostsItem> hostList= hostsReader.getHostsContent();
        ArrayList<Thread> threadArrayList=new ArrayList<>();
        final int threadnumber = threadNumber;
        for (int i = 0; i < threadnumber; i++) {
            final int a = i;
            threadArrayList.add(new Thread() {
                @Override
                public void run() {
                    int id = hostList.size()-a;
                    for (int j = 1; id > 0; j++) {
                        try {
                            switch (hostList.get(id-1).update(port,timeout,DNSServer)){
                                case -1:
                                    hostList.remove(id-1);
                                    break;
                                case 0:
                                    System.out.println("fail "+hostList.get(id-1).getDomain());
                                    problem++;
                                    break;
                                case 100:
                                    fixed++;
                                    break;
                                case 200:
                                    ;
                                    default:break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        n++;
                        id = hostList.size() - threadnumber * j - a;
                    }

                }
            });
        }

        for(Thread thread:threadArrayList){
            thread.start();
        }
        for(Thread thread:threadArrayList){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("All threads complete! "+fixed+" updated,"+problem+" fail");
        new HostsModify("hosts").writeHostsFile(hostList);

/*        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        current = n;
                        Thread.sleep(10000);
                        System.out.println("speed: " + (n - current + 1.0) / 10 + "/s");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();*/



    }

    public static String getDNSServer() {
        return DNSServer;
    }

    public static void setDNSServer(String DNSServer) {
        IPTest.DNSServer = DNSServer;
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
