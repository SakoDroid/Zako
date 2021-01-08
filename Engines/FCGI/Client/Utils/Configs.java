package Engines.FCGI.Client.Utils;

import java.util.HashMap;
import java.util.Map;

public class Configs {

    private static final HashMap<String,String[]> servers = new HashMap<>();

    public static int timeOut = 10000;
    public static byte[] padding = null;

    public static void addServer(String ext,Map data){
        String Host = (String) data.get("Host");
        String port = String.valueOf(data.get("Port"));
        servers.put(ext.trim(),new String[]{Host.trim(),port.trim()});
    }

    public static String[] getServer (String ext){
        return servers.get(ext);
    }
}
