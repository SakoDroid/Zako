package LoadBalancer;

import Server.Utils.JSON.JSONBuilder;
import java.io.File;
import java.util.*;

public class Configs {

    public static List<String[]> servers = new ArrayList<>();
    public static boolean on;
    public static int port;
    public static String host;
    public static SSLConfiguration ssl;

    public static void load(){
        HashMap data = (HashMap) JSONBuilder.newInstance().parse(new File(Server.Utils.Configs.Configs.baseAddress + "/Load_Balancer.conf")).toJava();
        on = (Boolean) data.get("ON");
        port = (int)(long) data.get("Port");
        host = String.valueOf(data.get("Host"));
        HashMap sslc = (HashMap) data.get("SSL");
        ssl = new SSLConfiguration((boolean) sslc.get("ON"), String.valueOf(data.get("jks path")), String.valueOf(data.get("jks pass")));
        ArrayList servs = (ArrayList) data.get("Servers");
        for (Object s : servs)
            addAServers(String.valueOf(s));
    }

    private static void addAServers(String server){
        if (!server.isEmpty()){
            if (server.contains("]") && server.contains("[")) {
                String[] temp = server.split("]", 2);
                servers.add(new String[]{temp[0].replace("[",""),(temp.length > 1) ? temp[1].trim() : "80"});
            } else {
                String[] temp = server.split(":", 2);
                if (temp.length > 1)
                    servers.add(temp);
                else
                    servers.add(new String[]{temp[0],"80"});
            }
        }
    }

    public static class SSLConfiguration{

        public final boolean SSL;
        public final String jks;
        public final String pss;

        public SSLConfiguration(boolean ss, String jk, String ps){
            SSL = ss;
            jks = jk;
            pss = ps;
        }
    }
}
