package service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by padeoe on 2016/3/18.
 */
public class HostsReader {
    private String hostsPath;

    public HostsReader(String hostsPath) {
        this.hostsPath = hostsPath;
    }

    /**
     * 获取hosts中的条目
     * @return
     */
    public List<HostsItem> getHostsContent(){
        ArrayList<HostsItem> hostsItemArrayList=new ArrayList<>();
        File hostsFile=new File(hostsPath);
        FileReader fileReader= null;
        try {
            fileReader = new FileReader(hostsFile);
            BufferedReader bufferedReader=new BufferedReader(fileReader);
            String line=null;
            HostsItem hostsItem_currentline=null;
            try {
                while((line=bufferedReader.readLine())!=null){
                    hostsItem_currentline=getHostItem(line);
                    if(hostsItem_currentline!=null){
                        hostsItemArrayList.add(hostsItem_currentline);
                    }
                }
                return hostsItemArrayList;
            } catch (IOException e) {
                System.out.println("文件读取时发生异常");
                e.printStackTrace();
                return null;
            }
        } catch (FileNotFoundException e) {
            System.out.println("未找到hosts文件."+"path:"+hostsPath);
            e.printStackTrace();
            return null;
        }
    }

    private static HostsItem getHostItem(String hostsLine){
        hostsLine=hostsLine.trim();
        int indexOfComment=hostsLine.indexOf("#");
        //整行只有注释
        if(indexOfComment==0){
            return null;
        }
        //如果存在注释，则去除注释
        if(indexOfComment!=-1){
            hostsLine=hostsLine.substring(0,indexOfComment);
        }
        String item[]=hostsLine.split(" ");
        if(item.length!=2) {
           // System.out.println("不合法的行"+hostsLine);
            return null;
        }
        return new HostsItem(item[0],item[1]);
    }

    public String getHostsPath() {
        return hostsPath;
    }
}
