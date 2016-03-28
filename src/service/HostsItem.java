package service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class represents the line in the "hosts"file,which is stored in
 * "C:\Windows\System32\drivers\etc\hosts" in Windows,"/etc/hosts"in Linux
 * HostsItem is made up of two parts,which are ip address and hostname.
 *
 * This class includes methods to update the ip address for its hostname
 * in two ways:search near ip of original ip or resolve the hostname by DNS
 *
 * Created by padeoe on 2016/3/18.
 */
public class HostsItem {
    private String domain;
    private String ip;

    public HostsItem(String ip, String domain) {
        this.ip = ip;
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String toString() {
        return ip + " " + domain;
    }

    /**
     * use DNSServer to resolve the hostname
     * @param host hostname
     * @param DNSServer DNS server address
     * @return
     */
    public static String DNS(String host, String DNSServer) {
        Runtime runtime = Runtime.getRuntime();
        String[] arg = new String[]{"nslookup", "-qt=AAAA", host, DNSServer};
        Process process;
        try {
            process = runtime.exec(arg);
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader bf = new BufferedReader(isr);
            String line;
            String resolvedIP = null;
            boolean ipStart = false;
            while ((line = bf.readLine()) != null) {
                if (ipStart) {
                    if (!line.trim().equals("")) {
                        resolvedIP = line.substring(line.indexOf(':') + 1, line.length()).trim();
                        break;
                    } else
                        break;
                } else {
                    if (line.startsWith("Name")) {
                        ipStart = true;
                    }
                }
            }
            return resolvedIP;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * update the ip address of the Hostname by resolve the hostname afresh,if the ip from dns server
     * is not reachable,it will ues  {@link #findNearIP(int, int)} to test it
     * @param DNSServer DNS server address
     * @return status code of whether DNS can return reachable ip,1 for success,0 for failure,-1 for non-existent domain
     */
    public int reDNS(String DNSServer,int port,int timeout) {
        String newip = DNS(domain, DNSServer);
        if (newip != null) {
            if (new IP(newip).isReachable(port, timeout)){
                ip = newip;
                return 1;//1 for success
            }
            return findNearIP(port,timeout) ? 1 : 0;//0 for failure
        } else {
            //   System.out.println("没有解析出"+domain);
            return -1;//-1 for non-existent domain
        }
    }

    /**
     * Usually,the next or the previous ip of the ip address in the
     * HostItem can also be used in "host" file.For this reason we
     * can create the method to find available ip for the hostname
     * quickly.in default,it will move forward and afterwards util six
     * @return whether we can find the reachable ip
     */
    public boolean findNearIP(int port,int timeout) {
        IP nextip = new IP(ip);
        for (int n = 0; n < 6; n++) {
            if ((nextip = nextip.next()).isReachable(port, timeout)) {
                System.out.println(new IP(ip) + " =>" + nextip.toString());
                ip = nextip.toString();
                return true;
            }
        }

        IP previousip = new IP(ip);
        for (int n = 0; n < 6; n++) {
            if ((previousip = previousip.previous()).isReachable(port, timeout)) {
                System.out.println(new IP(ip) + " =>" + previousip.toString());
                ip = previousip.toString();
                return true;
            }
        }

        return false;
    }

    /**
     * update the ip address for the hostname
     * if the ip is reachable,it will do nothing,
     * or it will call function {@link #findNearIP(int, int)} )} and {@link #reDNS(String, int, int)} in order
     * to find reachable ip
     * @param port the port used to test whether the ip is reachable
     * @param timeout max connect time to test whether the ip is reachable
     * @param DNSServer DNS server address when need DNS
     * @return
     */
    public int update(int port,int timeout,String DNSServer) {
        if (!new IP(ip).isReachable(port, timeout)) {
            if (!findNearIP(port,timeout)) {
                return reDNS(DNSServer,port,timeout);
            }
            return 100;//success
        } else
            return 200;//the ip is reachable originally
    }
}
