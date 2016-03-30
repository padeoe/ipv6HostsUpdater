package service;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

/**
 * This class represents the ip address,includes {@link Inet4Address}
 * and {@link Inet6Address}.it offers methods to get the next and the previous
 * ip address of current ip,and test whether the ip is reachable,the text format of the ip
 * Created by padeoe on 2016/3/18.
 */
public class IP {
    public byte[] address;

    public IP(String ipString) {
        try {
            InetAddress addr = InetAddress.getByName(ipString);
            address = addr.getAddress();

        } catch (UnknownHostException e) {
            System.out.println(ipString);
            e.printStackTrace();
        }
    }

    public IP(byte[] address) {
        this.address = address;
    }

    /**
     * get the next ip,for example the next ip,
     * of 127.0.0.1 is 127.0.0.2
     *
     * @return
     */
    public IP next() {
        byte[] result =address ;boolean needNext = true;int currentResult;int i;
        for (i = result.length - 1; i >= 0 && needNext; i--) {
            currentResult = result[i] + 1;
            result[i] = (byte) currentResult;
            needNext = currentResult == 0;
        }
        if (i == -1 && needNext) {
            System.out.println("overflow" + this.toString());
            return null;
        }
        return new IP(result);
    }

    /**
     * get the next ip,for example the next ip,
     * of 127.0.0.2 is 127.0.0.1
     *
     * @return
     */
    public IP previous(){
        byte[] result =address ;boolean needNext = true;int currentResult;int i;
        for (i = result.length - 1; i >= 0 && needNext; i--) {
            currentResult = result[i] - 1;
            result[i] = (byte) currentResult;
            needNext = currentResult == -1;
        }
        if (i == -1 && needNext) {
            System.out.println("overflow" + this.toString());
            return null;
        }
        return new IP(result);
    }

    /**
     * get the standard text format of the ip address
     *
     * @return
     */
    public String toString() {
        if (address.length == 4) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < address.length - 1; i++) {
                builder.append(address[i] & 0xFF);
                builder.append('.');
            }
            builder.append(address[address.length - 1] & 0xFF);
            return builder.toString();
        }
        if (address.length == 16) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < address.length - 3; i += 2) {
                int high = (address[i] & 0xff) << 8;
                int low = address[i + 1] & 0xff;
                int tmp = high + low;
                builder.append(Integer.toHexString(tmp));
                builder.append(':');
            }
            int high = (address[address.length - 2] & 0xff) << 8;
            int low = address[address.length - 1] & 0xff;
            int tmp = high + low;
            builder.append(Integer.toHexString(tmp));
            return builder.toString();
        }
        return null;

    }

    public boolean isReachable(int openPort, int timeOutMillis) {
        try {
            try (Socket soc = new Socket()) {
                soc.connect(new InetSocketAddress(this.toString(), openPort), timeOutMillis);
                soc.close();
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public boolean equals(IP another) {
        return Arrays.equals(address, another.address);
    }

    /**
     * Usually,the next or the previous ip of the ip address in the
     * HostItem can also be used in "host" file.For this reason we
     * can create the method to find available ip for the hostname
     * quickly.in default,it will move forward and afterwards util six
     *
     * @return whether we can find the reachable ip
     */
    public boolean findNearIP(int port, int timeout) {
        IP nextip = new IP(address.clone());
        for (int n = 0; n < 6; n++) {
            if ((nextip = nextip.next()).isReachable(port, timeout)) {
                System.out.println(this + " =>" + nextip.toString());
                this.address = nextip.address;
                return true;
            }
        }

        IP previousip = new IP(address.clone());
        for (int n = 0; n < 6; n++) {
            if ((previousip = previousip.previous()).isReachable(port, timeout)) {
                System.out.println(this + " =>" + previousip.toString());
                address = previousip.address;
                return true;
            }
        }

        return false;
    }


    public static boolean isReachable(String ip, int openPort, int timeOutMillis) {
        try {
            try (Socket soc = new Socket()) {
                soc.connect(new InetSocketAddress(ip, openPort), timeOutMillis);
                soc.close();
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

}
