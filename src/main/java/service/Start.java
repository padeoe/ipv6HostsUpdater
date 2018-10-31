package service;

import java.util.List;


/**
 * Created by padeoe on 2016/3/18.
 */
public class Start {
    static String dns[] = new String[]{"2001:4860:4860::8888", "2001:4860:4860::8844"};

    public static void main(String args[]) {
        if (args != null && args.length > 0) {
            String baseHostsPath = args[args.length - 1];
            generateHosts(baseHostsPath);
        } else {
            System.out.println("need one parameter: hosts file path");
        }
    }

    /**
     * It will update your hosts file for ipv6 network
     */
    public static void generateHosts(String baseHostsPath) {
        IPTest.setTimeout(800);
        IPTest.setThreadNumber(30);
        String dnsserver;
        if ((dnsserver = getAvailableDNS()) != null) {
            IPTest.setDNSServer(dnsserver);
            List<HostsItem> hostsItems = IPTest.testAllIP(baseHostsPath);
        } else {
            System.out.println("no available ipv6 dns server ");
        }
    }

    public static String getAvailableDNS() {
        for (int i = 0; i < dns.length; i++) {
            if (new IP(dns[i]).isReachable(53, 1000)) {
                return dns[i];
            }
        }
        return null;
    }
}
