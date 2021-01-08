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
        JSONBuilder bld = JSONBuilder.newInstance();
        JSONDocument doc = bld.parse(new File(Configs.getCWD() + "/CFGS/Zako.cfg"));
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
