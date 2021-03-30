import Server.Utils.*;
import java.io.*;
import java.util.HashMap;
import java.util.Objects;
import Engines.DDOS.Interface;
import Server.Utils.Configs.*;
import Server.Utils.JSON.*;

public class Loader {

    public static void load(boolean firstTime){
        Logger.ilog("Running test ...");
        System.out.println("Running test ...");
        if (basicUtils.runTest()) {
            Logger.ilog("Test OK! ...");
            System.out.println("Test OK! ...");
            Logger.ilog("Loading hosts configurations ...");
            System.out.println("Loading hosts configurations ...");
            for (String li : Objects.requireNonNull(new File(Configs.baseAddress).list())) {
                File fl = new File(Configs.baseAddress + "/" + li);
                if (fl.isDirectory()) {
                    Configs.loadAHost(fl);
                    SSLConfigs.load(fl);
                    FileTypes.load(fl);
                    CaptchaConfigs.load(fl);
                    ScriptsConfigs.load(fl);
                    Perms.load(fl);
                    APIConfigs.load(fl);
                    HTAccess.getInstance().load(fl);
                }
            }
            Configs.loadMain();
            Logger.ilog("Loading load balancer configurations ...");
            if (firstTime) {
                System.out.println("Loading load balancer configurations ...");
                LoadBalancer.Configs.load();
            }
            Logger.ilog("Loading ip blacklist ...");
            System.out.println("Loading ip blacklist ...");
            Perms.loadBlackList();
            Logger.ilog("Loading proxy configurations ...");
            System.out.println("Loading proxy configurations ...");
            ProxyConfigs.load();
            JSONBuilder bld = JSONBuilder.newInstance();
            JSONDocument doc = bld.parse(new File(Configs.baseAddress + "/Zako.conf"));
            HashMap data = (HashMap) doc.toJava();
            Interface.load((Boolean) data.get("DDOS Protection"));
            Logger.ilog("Loading basic utilities ...");
            basicUtils.load();
            Logger.ilog("ALL OK!");
        }else {
            Logger.ilog("Test failed. Run \"zako test\" in terminal to see what's wrong.");
            System.out.println("Test failed. Run \"zako test\" in terminal to see what's wrong.");
        }
    }
}