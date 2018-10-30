package service;

import java.util.List;

/**
 * Created by padeoe on 2016/4/22.
 */
public class DomainAnalysis {


    public static void main(String[] args) {
        output();
    }

    public static void output() {
        HostsReader hostsReader = new HostsReader("hosts");
        List<HostsItem> hostsItems = hostsReader.getHostsItemArrayList();
        hostsItems.stream().
                map(HostsItem::getDomain).
                filter(domain -> domain.indexOf(".googlevideo.com") != -1).
                map(domain -> domain.substring(0, domain.indexOf(".googlevideo.com")))
                ;

    }

}
