package Server.Utils.Configs;

import Server.Utils.JSON.JSONBuilder;
import Server.Utils.JSON.JSONDocument;
import java.io.File;
import java.util.HashMap;

public class SSLConfigs {

    private static final HashMap<String,SSLConfiguration> configs = new HashMap<>();

    public static void load(File fl){
        JSONBuilder bld = JSONBuilder.newInstance();
        JSONDocument doc = bld.parse(new File(fl.getAbsolutePath() + "/Main.conf"));
        HashMap data = (HashMap) doc.toJava();
        HashMap ssl = (HashMap) data.get("SSL");
        configs.put(String.valueOf(data.get("Name")),new SSLConfiguration((Boolean) ssl.get("ON"),(String) ssl.get("jks path"),(String) ssl.get("jks pass"),(HashMap) ssl.get("HTTPS only")));
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

    public static boolean isHTTPSOnly(String host){
        return configs.get(host).httpsOnly;
    }

    public static long getMaxAge(String host){
        return configs.get(host).maxAge;
    }

    public static boolean isSubdomainIncluded(String host){
        return configs.get(host).includeSubdomains;
    }

    private static class SSLConfiguration{

        public final boolean SSL;
        public final String jks;
        public final String pss;
        public boolean httpsOnly;
        public long maxAge;
        public boolean includeSubdomains;

        public SSLConfiguration(boolean ss, String jk, String ps,HashMap httpsOnlyData){
            SSL = ss;
            jks = jk;
            pss = ps;
            if (ss){
                this.httpsOnly = (Boolean) httpsOnlyData.get("Status");
                this.maxAge = (long) httpsOnlyData.get("Max Age");
                this.includeSubdomains = (Boolean) httpsOnlyData.get("Include subdomains");
            }
        }
    }
}
