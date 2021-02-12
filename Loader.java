import Server.Utils.*;
import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Engines.DDOS.Interface;
import Server.Utils.JSON.JSONBuilder;
import Server.Utils.JSON.JSONDocument;

public class Loader {

    public static boolean autoRs;
    private static File fl = null;

    public static void loadRs(){
        if (System.getProperty("os.name")
                .toLowerCase().contains("windows"))
            fl = new File(System.getProperty("user.dir") + "/Configs/Zako.cfg");
        else if (System.getProperty("os.name")
                .toLowerCase().contains("linux"))
            fl = new File("/etc/zako-web/Zako.cfg");
        JSONBuilder bld = JSONBuilder.newInstance();
        JSONDocument doc = bld.parse(fl);
        HashMap data = (HashMap) doc.toJava();
        autoRs = (Boolean) data.get("Auto Restart");
    }

    public static void load(){
        Logger.ilog("Loading basic utilities ...");
        basicUtils.load();
        Logger.ilog("Loading ssl configurations ...");
        SSLConfigs.load();
        Logger.ilog("Loading proxy configurations ...");
        ProxyConfigs.load();
        FileTypes.load();
        Logger.ilog("Loading CAPTCHA configurations ...");
        CaptchaConfigs.load();
        Logger.ilog("Loading scripts configurations ...");
        ScriptsConfigs.load();
        Logger.ilog("Loading configurations ...");
        Server.Utils.Configs.load();
        Logger.ilog("Loading permissions ...");
        Perms.load();
        Logger.ilog("Loading APIs configuration ...");
        APIConfigs.load();
        JSONBuilder bld = JSONBuilder.newInstance();
        JSONDocument doc = bld.parse(fl);
        HashMap data = (HashMap) doc.toJava();
        Interface.load((Boolean) data.get("DDOS Protection"), 200);
        Logger.ilog("ALL OK!");
    }
}