package service;

import java.io.*;

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
            InputStreamReader isr = new InputStreamReader(is, "GBK");
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
                    if (line.startsWith("名称")) {
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
     * is not reachable,it will ues {@link service.IP#findNearIP(int, int)} to test it
     * @param DNSServer DNS server address
     * @return status code of whether DNS can return reachable ip,1 for success,0 for failure,-1 for non-existent domain
     */
    public int reDNS(String DNSServer,int port,int timeout) {
        String newip = DNS(domain, DNSServer);
        if (newip != null) {
            if (new IP(newip).isReachable(port, timeout)){
                System.out.println(ip + " =>" + newip);
                ip = newip;
                return 1;//1 for success
            }
            IP currentip=new IP(newip);
            int status=currentip.findNearIP(port,timeout) ? 1 : 0;//0 for failure
            if(status==1){
                System.out.println(ip + " =>" + currentip.toString());
            }
            ip=currentip.toString();
            return status;
        } else {
            return -1;//-1 for non-existent domain
    }
    }

}
