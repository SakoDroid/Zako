import Server.Utils.*;
import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Engines.DDOS.Interface;
import Server.Utils.JSON.JSONBuilder;
import Server.Utils.JSON.JSONDocument;

public class Loader {

    public static boolean autoRs;

    public static void loadRs(){
        JSONBuilder bld = JSONBuilder.newInstance();
        JSONDocument doc = bld.parse(new File(Configs.getCWD() + "/CFGS/Zako.cfg"));
        HashMap data = (HashMap) doc.toJava();
        autoRs = (Boolean) data.get("Auto Restart");
    }

    public static void load(){
        Logger.ilog("Loading basic utilities ...");
        basicUtils.load();
        Logger.ilog("Loading ssl configurations ...");
        SSLConfigs.load();
        FileTypes.load();
        Logger.ilog("Loading scripts configurations ...");
        ScriptsConfigs.load();
        Logger.ilog("Loading configurations ...");
        Server.Utils.Configs.load();
        Logger.ilog("Loading permissions ...");
        Perms.load();
        Logger.ilog("Loading APIs configuration ...");
        APIConfigs.load();
        if (Server.Utils.Configs.isLBOn()) LoadBalancer.Configs.load();
        JSONBuilder bld = JSONBuilder.newInstance();
        JSONDocument doc = bld.parse(new File(Configs.getCWD() + "/CFGS/Zako.cfg"));
        HashMap data = (HashMap) doc.toJava();
        Interface.load((Boolean) data.get("DDOS Protection"), 200);
        Logger.ilog("ALL OK!");
    }
}