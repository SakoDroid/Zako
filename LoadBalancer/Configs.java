package LoadBalancer;

import Server.Utils.Logger;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Configs {

    public static List<String[]> servers = new ArrayList<>();
    public static String[] rvrz;

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

    private static void extractOtherServer(String cfgs){
        String ip = "";
        Pattern ptn = Pattern.compile("Reverse Proxy:[^-]*");
        Matcher mc = ptn.matcher(cfgs);
        if (mc.find()) ip = mc.group().replace("Reverse Proxy:","").trim().replace("\n","");
        rvrz = ip.split(":");
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
            extractOtherServer(cfgs);
            if (!servers.isEmpty()){
                Tracker.start();
                Logger.ilog("Load balancer is now active!");
            }
            else{
                Tracker.firstServer = rvrz;
                Logger.ilog("Reverse proxy enabled!");
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
