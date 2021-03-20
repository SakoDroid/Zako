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
        JSONDocument doc = bld.parse(new File((System.getProperty("os.name").toLowerCase().contains("linux") ?
                "/etc/zako-web/Zako.cfg" :
                "CFGS/Zako.cfg")));
        HashMap data = (HashMap) doc.toJava();
        Interface.load((Boolean) data.get("DDOS Protection"));
        Logger.ilog("ALL OK!");
    }
}