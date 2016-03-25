package service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
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

    public int reDNS(String DNSServer) {
        String newip = DNS(domain, DNSServer);
        if (newip != null) {
            ip = newip;
            if (new IP(newip).isReachable(80, 500))
                return 1;//解析成功
            return findNearIP() ? 1 : 0;//0表示失败
        } else {
            //   System.out.println("没有解析出"+domain);
            return -1;//域名无效
        }
    }

    public boolean findNearIP() {
        IP nextip = new IP(ip);
        for (int n = 0; n < 6; n++) {
            if ((nextip = nextip.next()).isReachable(80, 500)) {
                System.out.println(new IP(ip) + " =>" + nextip.toString());
                ip = nextip.toString();
                return true;
            }
        }

        IP previousip = new IP(ip);
        for (int n = 0; n < 6; n++) {
            if ((previousip = previousip.previous()).isReachable(80, 500)) {
                System.out.println(new IP(ip) + " =>" + previousip.toString());
                ip = previousip.toString();
                return true;
            }
        }

        return false;
    }

    public int update(int port,int timeout,String DNSServer) {
        if (!new IP(ip).isReachable(port, timeout)) {
            if (!findNearIP()) {
                return reDNS(DNSServer);
            }
            return 100;//寻找附近ip成功
        } else
            return 200;//ip自身可用
    }
}
