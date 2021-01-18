package Server.Utils;

import java.io.*;
import java.util.HashSet;

public class Perms {

    private final static HashSet<String> dirs = new HashSet<>();
    private final static HashSet<String> ipBlackList = new HashSet<>();
    private final static HashSet<String> ipsAuthorizedForPUTAndDelete = new HashSet<>();

    public static void load(){
        try{
            Logger.ilog("Loading static directories ...");
            BufferedReader bf = new BufferedReader(new FileReader(Configs.getCWD() + "/CFGS/Statics.cfg"));
            String line;
            while((line = bf.readLine()) != null){
                if (!line.startsWith("#") && !line.isEmpty()){
                    if ((line.endsWith("/"))) dirs.add(line);
                    else dirs.add(line + "/");
                }
            }
            bf.close();
            Logger.ilog("Loading ip black list ...");
            bf = new BufferedReader(new FileReader(Configs.getCWD() + "/CFGS/IP-Blacklist"));
            while((line = bf.readLine()) != null){
                if (!line.startsWith("#") && !line.isEmpty()) ipBlackList.add(line);
            }
            bf.close();
            Logger.ilog("Loading authorized ips for PUT and DELETE method ...");
            bf = new BufferedReader(new FileReader(Configs.getCWD() + "/CFGS/ILPD"));
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

    public static void addDir(String dir){
        if (dir.endsWith("/")) dirs.add(dir);
        else dirs.add(dir + "/");
    }

    public static int isDirPerm(String dir){
        int temp = 403;
        if (dir.endsWith("/getcp") || dir.endsWith("/chkcp")){
            temp = 200;
        }
        else{
            try {
                File fl = new File(dir);
                fl = new File(fl.getAbsolutePath().replace(fl.getName(),""));
                if (fl.isDirectory()) {
                    if (dirs.contains(dir)) temp = 200;
                } else {
                    if (dirs.contains(dir.replace(fl.getName(), ""))) {
                        if (fl.isFile()) temp = 200;
                        else temp = 404;
                    }
                }
            } catch (Exception ex) {
                String t = "";
                for (StackTraceElement a : ex.getStackTrace()) {
                    t += a.toString() + " ;; ";
                }
                t += ex.toString();
                Logger.ilog(t);
            }
        }
        return temp;
    }

    public static boolean isIPAllowedForPUTAndDelete(String ip){
        return ipsAuthorizedForPUTAndDelete.contains(ip);
    }

    public static boolean isIPAllowed(String ip){
        return !ipBlackList.contains(ip);
    }

    public static synchronized void addIPToBlackList(String ip){
        ipBlackList.add(ip);
        try(FileWriter fw = new FileWriter(Configs.getCWD() + "/CFGS/IP-Blacklist",true)){
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