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
        Server.Utils.Configs.loadMain();
        for (String li : Objects.requireNonNull(new File(Server.Utils.Configs.baseAddress).list())){
            File fl = new File(Server.Utils.Configs.baseAddress + "/" + li);
            if (fl.isDirectory()){
                Server.Utils.Configs.loadAHost(fl);
                SSLConfigs.load(fl);
                FileTypes.load(fl);
                CaptchaConfigs.load(fl);
                ScriptsConfigs.load(fl);
            }
        }
        Logger.ilog("Loading proxy configurations ...");
        System.out.println("Loading proxy configurations ...");
        ProxyConfigs.load();
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
        Logger.ilog("Loading basic utilities ...");
        basicUtils.load();
        Logger.ilog("ALL OK!");
    }
}