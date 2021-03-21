package Server.Utils;

import Server.Utils.JSON.JSONBuilder;
import Server.Utils.JSON.JSONDocument;
import java.io.File;
import java.util.HashMap;

public class SSLConfigs {

    private static final HashMap<String,SSLConfiguration> configs = new HashMap<>();

    public static void load(File fl){
        JSONBuilder bld = JSONBuilder.newInstance();
        JSONDocument doc = bld.parse(fl.getAbsolutePath() + "/Main.cfg");
        HashMap data = (HashMap) doc.toJava();
        HashMap ssl = (HashMap) data.get("SSL");
        configs.put(String.valueOf(data.get("Name")),new SSLConfiguration((Boolean) ssl.get("ON"),(String) ssl.get("jks path"),(String) ssl.get("jks pass")));
    }

    public static boolean isSSLOn(String host){
        return configs.get(host).SSL;
    }

    public static String getJKS(String host){
        return configs.get(host).jks;
    }

    public static String getPass(String host){
        return configs.get(host).pss;
    }

    private static class SSLConfiguration{

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
