package Server.Utils;

import Server.Utils.JSON.*;
import java.io.File;
import java.util.HashMap;

public class ProxyConfigs {

    public static boolean isOn = false;
    private static String[] address;

    public static void load(){
        File fl = null;
        if (System.getProperty("os.name")
                .toLowerCase().contains("windows"))
            fl = new File(System.getProperty("user.dir") + "/Configs/Zako.cfg");
        else if (System.getProperty("os.name")
                .toLowerCase().contains("linux"))
            fl = new File("/etc/zako-web/Zako.cfg");
        JSONBuilder bld = JSONBuilder.newInstance();
        JSONDocument doc = bld.parse(fl);
        HashMap data = (HashMap) doc.toJava();
        HashMap proxy = (HashMap) data.get("Proxy");
        isOn = (Boolean) proxy.get("ON");
        if (isOn){
            String host = String.valueOf(proxy.get("Host"));
            long port = (Long) proxy.get("Port");
            address = new String[]{host,String.valueOf(port)};
        }
    }

    public static String[] getAddress(){
        return address;
    }
}
