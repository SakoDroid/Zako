import LoadBalancer.Reporter;
import Server.Utils.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Server.DDOS.Interface;

public class Loader {

    public static void load(){
        new Reporter();
        Logger.ilog("Loading basic utilities ...");
        basicUtils.load();
        Logger.ilog("Loading configurations ...");
        Server.Utils.Configs.load();
        Logger.ilog("Loading permissions ...");
        Perms.load();
        Logger.ilog("Loading APIs configuration ...");
        APIConfigs.load();
        if (Server.Utils.Configs.isLBOn()) LoadBalancer.Configs.load();
        try(BufferedReader bf = new BufferedReader(new FileReader(Configs.getCWD() + "/src/CFGS/Main.cfg"))){
            String cfgs = "",line;
            while((line = bf.readLine()) != null){
                if (!line.startsWith("#"))cfgs += line + "\n";
            }
            Pattern ptn = Pattern.compile("DDOS-Protection=.*");
            Matcher mc = ptn.matcher(cfgs);
            int ddos = 1;
            if (mc.find())ddos = Integer.parseInt(mc.group().replace("DDOS-Protection=","").replace("\"",""));
            ptn = Pattern.compile("DDOS-Allowable-Time-Between-Requests=.*");
            mc = ptn.matcher(cfgs);
            long time = 200;
            if (mc.find()) time = Long.parseLong(mc.group().replace("DDOS-Allowable-Time-Between-Requests=","").replace("\"",""));
            Interface.load(ddos,time);
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
        Logger.ilog("ALL OK!");
    }
}