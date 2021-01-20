package Server.Utils;

import java.io.*;
import java.util.HashSet;

public class Perms {

    private final static HashSet<String> ipBlackList = new HashSet<>();
    private final static HashSet<String> ipsAuthorizedForPUTAndDelete = new HashSet<>();

    public static void load(){
        try{
            String line;
            Logger.ilog("Loading ip black list ...");
            BufferedReader bf = new BufferedReader(new FileReader("/etc/zako-web/sec/IP-Blacklist"));
            while((line = bf.readLine()) != null){
                if (!line.startsWith("#") && !line.isEmpty()) ipBlackList.add(line);
            }
            bf.close();
            Logger.ilog("Loading authorized ips for PUT and DELETE method ...");
            bf = new BufferedReader(new FileReader("/etc/zako-web/sec/ILPD"));
            while((line = bf.readLine()) != null){
                if (!line.startsWith("#") && !line.isEmpty()) ipsAuthorizedForPUTAndDelete.add(line);
            }
            bf.close();
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    public static boolean isIPAllowedForPUTAndDelete(String ip){
        return ipsAuthorizedForPUTAndDelete.contains(ip);
    }

    public static boolean isIPAllowed(String ip){
        return !ipBlackList.contains(ip);
    }

    public static synchronized void addIPToBlackList(String ip){
        ipBlackList.add(ip);
        try(FileWriter fw = new FileWriter("/etc/zako-web/sec/IP-Blacklist",true)){
            fw.write("\n" + ip);
            fw.flush();
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