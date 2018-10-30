package ipv4service;

import service.HostsModify;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by padeoe on 2016/4/3.
 */
public class HostMoidfy_v4 extends HostsModify {
    HostMoidfy_v4(String hostsPath){
        super(hostsPath);
    }

    @Override
    public String getTitle() {
        StringBuilder hostBuilder=new StringBuilder();
        hostBuilder.append("#+=======================================================+");
        hostBuilder.append(System.getProperty("line.separator"));
        hostBuilder.append("#+                Author: padeoe@gmail.com               +");
        hostBuilder.append(System.getProperty("line.separator"));
        hostBuilder.append("#+  Project: https://github.com/padeoe/ipv6HostsUpdater  +");
        hostBuilder.append(System.getProperty("line.separator"));
        hostBuilder.append("#+            Update @ http://padeoe.com/liantonghosts           +");
        hostBuilder.append(System.getProperty("line.separator"));
        hostBuilder.append("#+             updated at:");
        hostBuilder.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
        hostBuilder.append("            +");
        hostBuilder.append(System.getProperty("line.separator"));
        hostBuilder.append("#+=======================================================+");
        hostBuilder.append(System.getProperty("line.separator"));
        return hostBuilder.toString();
    }
}
