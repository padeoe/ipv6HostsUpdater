package service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by padeoe on 2016/3/18.
 */
public class IPTest {
    static int n = 0;
    static int current = 0;
    static int fixed=0;
    static int problem=0;
    public static void testAllIP(){
        HostsReader hostsReader=new HostsReader("C:\\Windows\\System32\\drivers\\etc\\hosts");
        List<HostsItem> hostList= hostsReader.getHostsContent();
        ArrayList<Thread> threadArrayList=new ArrayList<>();
        final int threadnumber = 200;
        for (int i = 0; i < threadnumber; i++) {
            final int a = i;
            threadArrayList.add(new Thread() {
                @Override
                public void run() {
                    int id = hostList.size()-a;
                    for (int j = 1; id > 0; j++) {
                        try {
                            String availableIP;
                            if((availableIP=testArroundIP(hostList.get(id-1).getIp()))!=null){
                                hostList.get(id-1).setIp(availableIP);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        n++;
                      //  System.out.println(id);
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
        System.out.println("所有线程已执行完毕.修复了:"+fixed+"无法修复："+problem);
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

    public static String testArroundIP(String needTestIP){
        IP ip=new IP(needTestIP);
        if(!ip.isReachable(80,500)){
            IP testip;
            for(int n=0;n<6;n++){
                if((testip=ip.next()).isReachable(80,500)){
                    System.out.println(needTestIP+" =>"+testip.toString());
                    fixed++;
                    return testip.toString();
                }
            }
            System.out.println(needTestIP);
            problem++;
            return null;
        }
        return null;
    }
}
