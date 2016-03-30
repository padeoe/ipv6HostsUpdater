package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by padeoe on 2016/3/30.
 */
public class HostsMap {
    private Map<String,ArrayList<String>> hostsMap=new HashMap<>();
    public void add(HostsItem hostsItem){
        if(hostsItem!=null){
            if(hostsMap.containsKey(hostsItem.getIp())){
                hostsMap.get(hostsItem.getIp()).add(hostsItem.getDomain());
            }
            else{
                ArrayList<String>domainList=new ArrayList<>();
                domainList.add(hostsItem.getDomain());
                hostsMap.put(hostsItem.getIp(),domainList);
            }
        }
    }

    public String[] IPArray(){
        return hostsMap.keySet().toArray(new String[hostsMap.size()]);
    }

    public ArrayList<String>getHostName(String ip){
        return hostsMap.get(ip);
    }

    public Set<Map.Entry<String, ArrayList<String>>> entrySet() {
        return hostsMap.entrySet();
    }
}
