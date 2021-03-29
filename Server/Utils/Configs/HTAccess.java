package Server.Utils.Configs;

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

    private static class HTAccessConfig {

        private final boolean upgradeIsAllowed;
        private final boolean KA;
        private final int keepAliveTimeOut;
        private final int MNORPC;
        private final HashSet<String> allowableUpgrades = new HashSet<>();
        private ArrayList<String> lastModifiedToBeSent;
        private ArrayList<String> ETagToBeSent;

        public HTAccessConfig(File fl){
            HashMap mainData = (HashMap) JSONBuilder.newInstance().parse(new File(fl.getAbsolutePath() + "/htaccess.conf")).toJava();
            upgradeIsAllowed = (Boolean) mainData.get("allow upgrades");
            keepAliveTimeOut = (int)(long) mainData.get("keep alive default timeout");
            KA = (Boolean) mainData.get("Keep Alive");
            MNORPC = (int)(long) mainData.get("MNORPC");
            String allowableUpgradesList = String.valueOf(mainData.get("Allowable protocols"));
            for (String prot : allowableUpgradesList.split(" "))
                allowableUpgrades.add(prot.trim());
            this.fixTheUpgradeList();
            HashMap cond = (HashMap) mainData.get("Conditionals");
            lastModifiedToBeSent = (ArrayList<String>)cond.get("Last-Modified");
            ETagToBeSent = (ArrayList<String>) cond.get("ETag");

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
