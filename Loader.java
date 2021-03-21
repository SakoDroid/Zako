import Server.Utils.*;
import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Engines.DDOS.Interface;
import Server.Utils.JSON.JSONBuilder;
import Server.Utils.JSON.JSONDocument;

public class Loader {

    public static void load(){
        Logger.ilog("Loading hosts configurations ...");
        System.out.println("Loading hosts configurations ...");
        for (String li : Objects.requireNonNull(new File(Server.Utils.Configs.baseAddress).list())){
            File fl = new File(Server.Utils.Configs.baseAddress + "/" + li);
            if (fl.isDirectory()){
                Server.Utils.Configs.loadAHost(fl);
                SSLConfigs.load(fl);
                FileTypes.load(fl);
                CaptchaConfigs.load(fl);
                ScriptsConfigs.load(fl);
                Perms.load(fl);
                APIConfigs.load(fl);
            }
        }
        Server.Utils.Configs.loadMain();
        Logger.ilog("Loading ip blacklist ...");
        System.out.println("Loading ip blacklist ...");
        Perms.loadBlackList();
        Logger.ilog("Loading proxy configurations ...");
        System.out.println("Loading proxy configurations ...");
        ProxyConfigs.load();
        JSONBuilder bld = JSONBuilder.newInstance();
        JSONDocument doc = bld.parse(new File(Configs.baseAddress + "/Zako.cfg"));
        HashMap data = (HashMap) doc.toJava();
        Interface.load((Boolean) data.get("DDOS Protection"));
        Logger.ilog("Loading basic utilities ...");
        basicUtils.load();
        Logger.ilog("ALL OK!");
    }
}