package LoadBalancer;

import Server.Utils.Logger;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Configs {

    public static List<String[]> servers = new ArrayList<>();

    private static void extractZakoServers(String cfgs){
        String ips = "";
        Pattern ptn = Pattern.compile("Zako Servers:[^-]*");
        Matcher mc = ptn.matcher(cfgs);
        if (mc.find()) ips = mc.group().replace("Zako Servers:","").trim();
        for (String line : ips.split("\n")){
            if (!line.isEmpty()){
                String[] server = line.split(":");
                if (server.length > 1) servers.add(server);
                else servers.add(new String[]{line.trim(),"80"});
            }
        }
    }


    public static void load(){
        try(BufferedReader bf = new BufferedReader(new FileReader(Server.Utils.Configs.getCWD() + "/CFGS/Load_Balancer.cfg"))){
            Logger.ilog("Loading load balancer ...");
            String cfgs = "";
            String line;
            while((line = bf.readLine()) != null){
                if (!line.startsWith("#")) cfgs += line + "\n";
            }
            extractZakoServers(cfgs);
            if (!servers.isEmpty()){
                Tracker.start();
                Logger.ilog("Load balancer is now active!");
            }
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }
}
