package service;

import service.*;

import java.util.List;


/**
 * Created by padeoe on 2016/3/18.
 */
public class Start {
    static String dns[]=new String[]{"2001:4860:4860::8888","2001:da8:1007:3::101"};
    public static void main(String a[]){
        updateHosts();
    }

    public static void updateHosts(){
        IPTest.setTimeout(800);
        IPTest.setThreadNumber(30);
        String dnsserver;
        if((dnsserver=getAvailableDNS())!=null){
            IPTest.setDNSServer(dnsserver);
            List<HostsItem> hostsItems=IPTest.testAllIP();
            new HostsModify("C:\\Windows\\System32\\drivers\\etc\\hosts").writeHostsFile(hostsItems);
        }
        else{
            System.out.println("no available ipv6 dns server ");
        }
    }

    public static String getAvailableDNS(){
        for(int i=0;i<dns.length;i++){
            if(new IP(dns[i]).isReachable(53,1000)){
                return dns[i];
            }
        }
        return null;
    }
}
