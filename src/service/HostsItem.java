package service;

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

    public String toString(){
        return ip+" "+domain;
    }
}
