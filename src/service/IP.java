package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
            return builder.toString().replaceAll(":0:(0:)+","::");
        }
        return null;

    }

    public boolean isReachable(int openPort, int timeOutMillis) {
        return isReachable(this.toString(),openPort,timeOutMillis);
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
                this.address = nextip.address;
                return true;
            }
        }

        IP previousip = new IP(address.clone());
        for (int n = 0; n < 6; n++) {
            if ((previousip = previousip.previous()).isReachable(port, timeout)) {
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

    public static boolean isReachableByPing(String ip, int openPort, int timeOutMillis) {
        Runtime runtime = Runtime.getRuntime();
        String[] arg = new String[]{"ping",ip, "-n", "1", "-w",String.valueOf(timeOutMillis)};
        Process process;
        try {
            process = runtime.exec(arg);
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "GBK");
            BufferedReader bf = new BufferedReader(isr);
            String line;
            boolean ipStart = false;
            while ((line = bf.readLine()) != null) {
                if (ipStart) {
                    if(line.endsWith("(0% 丢失)，")){
                        return true;
                    }
                    if(line.endsWith("(100% 丢失)，")){
                        return false;
                    }
                    System.out.println(line);
                } else {
                    if (line.startsWith(ip)) {
                        ipStart = true;
                    }
                }
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断一个IP是否是可以访问的Google搜索服务器的IP。通过对Google搜索服务器的一张图片的HTTP访问进行判断
     *
     * @return IP是否是可以访问的Google搜索服务器的IP
     */
    public boolean isAvailableGoogleSearchIP(int timeout) {
        URL url ;
        try {
            url = new URL("http://" + this.toString() + "/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png");
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            InputStream inputStream = connection.getInputStream();
            inputStream.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
