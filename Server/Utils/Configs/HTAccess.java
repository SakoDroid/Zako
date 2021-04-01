package Server.Utils.Configs;

import Server.Utils.Enums.Methods;
import Server.Utils.JSON.JSONBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class HTAccess {

    private static final HTAccess hta = new HTAccess();
    private final HashMap<String,HTAccessConfig> configs = new HashMap<>();

    private HTAccess(){}

    public void load(File fl){
        configs.put(fl.getName(), new HTAccessConfig(fl));
    }

    public static HTAccess getInstance(){
        return hta;
    }

    public boolean isUpAllowed(String host){
        return this.configs.get(host).upgradeIsAllowed;
    }

    public int getKeepAliveTimeout(String host){
        return this.configs.get(host).keepAliveTimeOut;
    }

    public int getMNORPC(String host){
        return this.configs.get(host).MNORPC;
    }

    public long getAccessControlMaxAge(String host){
        return this.configs.get(host).maxAge;
    }

    public boolean isKeepAliveAllowed(String host){
        return this.configs.get(host).KA;
    }

    public boolean isUpgradePermitted(String upgrade, String host){
        return this.configs.get(host).allowableUpgrades.contains(upgrade.trim());
    }

    public boolean shouldLMBeSent(String path, String host){
        return this.configs.get(host).lastModifiedToBeSent.contains(path);
    }

    public boolean shouldETagBeSent(String path, String host){
        return this.configs.get(host).ETagToBeSent.contains(path);
    }

    public boolean isCompressionAllowed(String host){
        return this.configs.get(host).allowCompression;
    }

    public boolean isOriginAllowed(String origin,String host){
        if (origin.equals(host))
            return this.configs.get(host).permittedOrigins.contains("self");
        else
            return this.configs.get(host).permittedOrigins.contains(origin);
    }

    public boolean isMethodAllowed(Methods method, String host){
        return this.configs.get(host).permittedMethods.contains(method);
    }

    public boolean isCredentialsAllowed(String host){
        return this.configs.get(host).allowCredentials;
    }

    public ArrayList<Methods> getAllowableMethods(String host){
        return this.configs.get(host).permittedMethods;
    }

    public ArrayList<String> getAllowableHeaders(String host){
        return this.configs.get(host).permittedHeaders;
    }

    private static class HTAccessConfig {

        private final boolean upgradeIsAllowed;
        private final boolean KA;
        private final boolean allowCompression;
        private final boolean allowCredentials;
        private final int keepAliveTimeOut;
        private final int MNORPC;
        private final long maxAge;
        private final HashSet<String> allowableUpgrades = new HashSet<>();
        private final ArrayList<String> lastModifiedToBeSent;
        private final ArrayList<String> ETagToBeSent;
        private final ArrayList<Methods> permittedMethods = new ArrayList<>();
        private final ArrayList<String> permittedOrigins;
        private final ArrayList<String> permittedHeaders;

        public HTAccessConfig(File fl){
            HashMap mainData = (HashMap) JSONBuilder.newInstance().parse(new File(fl.getAbsolutePath() + "/htaccess.conf")).toJava();
            upgradeIsAllowed = (Boolean) mainData.get("allow upgrades");
            keepAliveTimeOut = (int)(long) mainData.get("keep alive default timeout");
            KA = (Boolean) mainData.get("Keep Alive");
            MNORPC = (int)(long) mainData.get("MNORPC");
            allowCompression = (Boolean) mainData.get("Allow compression");
            String allowableUpgradesList = String.valueOf(mainData.get("Allowable protocols"));
            for (String prot : allowableUpgradesList.split(" "))
                allowableUpgrades.add(prot.trim());
            this.fixTheUpgradeList();
            HashMap cond = (HashMap) mainData.get("Conditionals");
            lastModifiedToBeSent = (ArrayList<String>)cond.get("Last-Modified");
            ETagToBeSent = (ArrayList<String>) cond.get("ETag");
            HashMap cors = (HashMap) mainData.get("CORS");
            allowCredentials = (Boolean) cors.get("Allow-Credentials");
            ArrayList<String> pm = (ArrayList<String>) cors.get("Permitted methods");
            permittedOrigins = (ArrayList<String>) cors.get("Permitted origins");
            permittedHeaders = (ArrayList<String>) cors.get("Permitted headers");
            maxAge = (long) cors.get("Access-Control-Max-Age");
            for (String m : pm)
                permittedMethods.add(Methods.valueOf(m.toUpperCase()));
        }

        private void fixTheUpgradeList(){
            if (allowableUpgrades.contains("HTTP/2")){
                allowableUpgrades.add("http/2");
                allowableUpgrades.add("h2");
                allowableUpgrades.add("h2c");
            }
            if (allowableUpgrades.contains("HTTP/1.1"))
                allowableUpgrades.add("http/1.1");
        }
    }
}
