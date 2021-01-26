package Server.Utils;

import java.io.*;
import java.util.HashSet;

public class Perms {

    private final static HashSet<String> ipBlackList = new HashSet<>();
    private final static HashSet<String> ipsAuthorizedForPUTAndDelete = new HashSet<>();

    public static void load(){
        try{
            File fl = null;
            if (System.getProperty("os.name")
                    .toLowerCase().contains("windows"))
                fl = new File(System.getProperty("user.dir") + "/Configs/sec/IP-Blacklist");
            else if (System.getProperty("os.name")
                    .toLowerCase().contains("linux"))
                fl = new File("/etc/zako-web/sec/IP-Blacklist");
            String line;
            Logger.ilog("Loading ip black list ...");
            assert fl != null;
            BufferedReader bf = new BufferedReader(new FileReader(fl));
            while((line = bf.readLine()) != null){
                if (!line.startsWith("#") && !line.isEmpty()) ipBlackList.add(line);
            }
            bf.close();
            Logger.ilog("Loading authorized ips for PUT and DELETE method ...");
            if (System.getProperty("os.name")
                    .toLowerCase().contains("windows"))
                fl = new File(System.getProperty("user.dir") + "/Configs/sec/ILPD");
            else if (System.getProperty("os.name")
                    .toLowerCase().contains("linux"))
                fl = new File("/etc/zako-web/sec/ILPD");
            bf = new BufferedReader(new FileReader(fl));
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
        File fl = null;
        if (System.getProperty("os.name")
                .toLowerCase().contains("windows"))
            fl = new File(System.getProperty("user.dir") + "/Configs/sec/IP-Blacklist");
        else if (System.getProperty("os.name")
                .toLowerCase().contains("linux"))
            fl = new File("/etc/zako-web/sec/IP-Blacklist");
        try(FileWriter fw = new FileWriter(fl,true)){
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