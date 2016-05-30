package domain_analysis;

import service.HostsItem;
import service.HostsModify;
import service.NewHostReader;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * used to generate the domain in hosts file
 * Created by padeoe on 2016/5/24.
 */
public class DomainGenerator {
    public static void main(String[] args) {
       // completeDomainSeries();
       // addAllHosts("C:\\Users\\padeoe\\Desktop\\hosts","C:\\Windows\\System32\\drivers\\etc\\hosts","C:\\Users\\padeoe\\Desktop\\hosts_new");
    }

    /**
     * merge two hosts file,drop repeated lines
     * @param masterHostsFilePath host file path,the lines in this file remain if need merging
     * @param addedHostsFilePath host file path,the lines in this file will be dropped if need merging
     * @param newHostsFilePath
     */
    public static void addAllHosts(String masterHostsFilePath,String addedHostsFilePath,String newHostsFilePath){
        Map<String,HostsItem>domainMap=new NewHostReader(addedHostsFilePath).getDomainMap();
        domainMap.putAll(new NewHostReader(masterHostsFilePath).getDomainMap());
        List<HostsItem>newHostItemList=domainMap.values().parallelStream().collect(Collectors.toList());
        System.out.println(newHostItemList.size());
        HostsModify hostsModify=new HostsModify(newHostsFilePath);
        hostsModify.writeHostsFile(newHostItemList);
    }

    /**
     *
     * @param originHostsPath
     * @param newHostsPath
     */
    public static void completeDomainSeries(String originHostsPath,String newHostsPath){
        NewHostReader hostReader = new NewHostReader(originHostsPath);
        List<HostsItem> hostsItems = hostReader.getHostsItemArrayList();
        Map<String, String> patternMap = getAlldomainPattern(hostsItems);
        System.out.println(patternMap.size());
        for (String domain : patternMap.values()) {
            hostsItems.addAll(generateDomainNumberSeries(domain, 50));
        }
        System.out.println(hostsItems.size());
        HostsModify hostsModify = new HostsModify(newHostsPath);
        hostsModify.writeHostsFile(hostsItems);
    }

    /**
     * used to generate the domains with given google domain pattern.
     * for example,when you input domain "r15---sn-vgqs7ne6.googlevideo.com"
     * this function will return a list include "r16---sn-vgqs7ne6.googlevideo.com","r17---sn-vgqs7ne6.googlevideo.com",
     * the function will use DNS to verify whether the  generated domains exist and drop those not exist
     * @param domain
     * @param threadNumber
     * @return
     */
    public static List<HostsItem> generateDomainNumberSeries(String domain, int threadNumber) {
        ArrayList<HostsItem> domainSeries = new ArrayList<>();//储存结果
        ArrayList<Integer> index = new ArrayList<>();//储存数字所在的位置，包含了域名中数字的个数
        //开始扫描
        for (int i = 0; i < domain.length(); i++) {
            if (domain.charAt(i) <= '9' && domain.charAt(i) >= '0') {
                index.add(i);
            }
        }
        int n_test = (int) Math.pow(10, index.size());
        if (index.size()>=1&&index.size()<=5) {
            ArrayList<Thread> threads = new ArrayList<>();
            final int threadnumber = threadNumber;
            for (int i = 0; i < threadNumber; i++) {
                final int offset = i;
                threads.add(new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        int id = n_test - offset;
                        for (int j = 1; id > 0; j++) {
                            int number = id - 1;
                            HostsItem hostsItem = new HostsItem("", getNewDomain(domain, index, number));
                           // System.out.println(hostsItem.getDomain());
                            if (HostsItem.DNS(hostsItem.getDomain(), "2001:4860:4860::8888") != null) {
                                if (hostsItem.reDNS("2001:4860:4860::8888", 443, 800) == 1) {
                                    domainSeries.add(hostsItem);
                                    System.out.println(hostsItem.getIp()+" "+hostsItem.getDomain());
                                }
                            }
                            id = n_test - threadnumber * j - offset;
                        }
                    }
                });
            }
            threads.forEach(thread -> thread.start());
        }

        return domainSeries;

    }

    public static String getNewDomain(String oldDomain, ArrayList<Integer> index, int number) {
        String numberString = String.format("%0" + index.size() + "d", number);
        char[] domain = oldDomain.toCharArray();
        for (int i = 0; i < index.size(); i++) {
            domain[index.get(i)] = numberString.charAt(i);
        }
        return new String(domain);
    }

    public static Map<String, String> getAlldomainPattern(List<HostsItem> hostsItems) {
        Map<String, String> patternMap = new HashMap<>();
        for (HostsItem hostsItem : hostsItems) {
            String pattern = getdomainPattern(hostsItem.getDomain());
            if (!patternMap.containsKey(pattern)) {
                patternMap.put(pattern, hostsItem.getDomain());
            }
        }
        return patternMap;
    }

    public static String getdomainPattern(String domain) {
        ArrayList<Character> pattern = new ArrayList<>();
        for (int i = 0; i < domain.length(); i++) {
            if (domain.charAt(i) > '9' || domain.charAt(i) < '0') {
                pattern.add(domain.charAt(i));
            }
        }
        StringBuilder builder = new StringBuilder();
        pattern.forEach(character -> builder.append(character));
        return builder.toString();
    }
}
