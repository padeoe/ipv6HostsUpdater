import service.*;

/**
 * Created by padeoe on 2016/3/18.
 */
public class Start {
    public static void main(String a[]){
        IPTest.setTimeout(800);
        IPTest.setThreadNumber(50);
        IPTest.setDNSServer("2001:4860:4860::8888");
        IPTest.testAllIP();
    }
}
