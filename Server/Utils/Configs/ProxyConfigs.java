package Server.Utils.Configs;

import Server.Utils.JSON.*;
import java.io.File;
import java.util.HashMap;

public class ProxyConfigs {

    public static boolean isOn = false;
    private static String[] address;

    public static void load(){
        JSONBuilder bld = JSONBuilder.newInstance();
        JSONDocument doc = bld.parse(new File(Configs.baseAddress + "/Zako.conf"));
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
