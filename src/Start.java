import service.*;


/**
 * Created by padeoe on 2016/3/18.
 */
public class Start {
    public static void main(String a[]){
        IPTest.setTimeout(800);
        IPTest.setThreadNumber(100);
        IPTest.setDNSServer("fd87:6259:8a27::1");
        IPTest.testAllIP();

    }
}
