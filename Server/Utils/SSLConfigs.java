package Server.Utils;

import Server.Utils.JSON.JSONBuilder;
import Server.Utils.JSON.JSONDocument;
import java.io.File;
import java.util.HashMap;

public class SSLConfigs {

    public static boolean SSL;
    private static String jks;
    private static String pss;

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
        HashMap ssl = (HashMap) data.get("SSL");
        SSL = (Boolean) ssl.get("ON");
        jks = (String) ssl.get("jks path");
        pss = (String) ssl.get("jks pass");
    }

    public static String getJKS(){
        return jks;
    }

    public static String getPass(){
        return pss;
    }
}
