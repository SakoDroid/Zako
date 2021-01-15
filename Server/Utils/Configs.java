package Server.Utils;

import Server.Utils.JSON.*;
import java.io.*;
import java.util.HashMap;
import java.util.regex.*;

public class Configs {

    private static final HashMap<String,HashMap<String,String>> Configs = new HashMap<>();
    private static final HashMap<String,Integer> hostsStatus = new HashMap<>();
    private static final HashMap<String,String[]> HostFrAdd = new HashMap<>();

    private static boolean loadBalancer;
    private static boolean webServer;
    private static int LBPort = 80;
    private static int WSPort = 80;

    public static boolean captcha;
    public static boolean cache;
    public static boolean keepAlive;
    public static long generalSize = Long.MAX_VALUE;
    public static long fileSize = Long.MAX_VALUE;
    public static long postBodySize = Long.MAX_VALUE;
    public static int timeout;
    public static int captchaLength = 5;
    public static int captchaHardness = 5;
    public static String MainHost;
    public static String MainHostWithPort;

    private Configs(){}

    private static void parseCfg(String cfg){
        String Host = "";
        String MainDir = "";
        String CGIDir = "";
        String LogsDir = "";
        String TempUploadDir = "";

        Pattern ptn = Pattern.compile("Host=.*");
        Matcher mc = ptn.matcher(cfg);
        if(mc.find()) Host = mc.group().replace("Host=","");
        ptn = Pattern.compile("RootDir=.*");
        mc = ptn.matcher(cfg);
        if(mc.find()) MainDir = mc.group().replace("RootDir=","");
        File Main = new File(MainDir);
        if (!Main.isDirectory()) Main.mkdir();
        ptn = Pattern.compile("CGIDir=.*");
        mc = ptn.matcher(cfg);
        if(mc.find()) CGIDir = mc.group().replace("CGIDir=","");
        File CGI = new File(CGIDir);
        if (!CGI.isDirectory()) CGI.mkdir();
        ptn = Pattern.compile("LogsDir=.*");
        mc = ptn.matcher(cfg);
        if(mc.find()) LogsDir = mc.group().replace("LogsDir=","");
        File Lgs = new File(LogsDir);
        if (!Lgs.isDirectory()) Lgs.mkdir();
        ptn = Pattern.compile("TempUploadDir=.*");
        mc = ptn.matcher(cfg);
        if(mc.find()) TempUploadDir = mc.group().replace("TempUploadDir=","");
        File Up = new File(TempUploadDir);
        if (!Up.isDirectory()) Up.mkdir();
        if(Configs.get(Host) != null){
            HashMap<String,String> temp = new HashMap<>();
            Configs.put(Host,temp);
        }
        Perms.addDir(MainDir);
        Perms.addDir(CGIDir);
        Configs.get(Host).put("Main", MainDir);
        Configs.get(Host).put("Logs", LogsDir);
        Configs.get(Host).put("CGI", CGIDir);
        Configs.get(Host).put("Up", TempUploadDir);
        if (cfg.contains("%%MAIN%%")) {
            if(Host.contains(":")){
                MainHost = Host.split(":")[0];
                MainHostWithPort = Host;
            }
            else{
                MainHost = Host;
                MainHostWithPort = Host + ((SSLConfigs.SSL)? "443":"80");
            }
            HashMap<String, String> temp = new HashMap<>();
            temp.put("Main", MainDir);
            temp.put("Logs", LogsDir);
            temp.put("CGI", CGIDir);
            temp.put("Up", TempUploadDir);
            Configs.put("Main", temp);
        }
    }

    private static void loadDirs(){
        try(FileReader bf = new FileReader(System.getProperty("user.dir") + "/CFGS/Dirs.cfg")){
            File tempdir = new File(getCWD() + "/Temp");
            if (!tempdir.isDirectory()) tempdir.mkdir();
            String cfg = "";
            int i;
            while((i = bf.read()) != -1){
                cfg += (char)i;
            }
            Pattern ptn = Pattern.compile("Host=[^#]+");
            Matcher mc = ptn.matcher(cfg);
            while(mc.find()) parseCfg(mc.group());
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);

        }
    }

    private static void addFr(String line){
        String[] ln = line.split("->");
        hostsStatus.put(ln[0].trim(),1);
        String[] add = ln[1].trim().split(":");
        if (add.length > 1) HostFrAdd.put(ln[0].trim(),add);
        else HostFrAdd.put(ln[0].trim(),new String[]{add[0],"80"});
    }

    private static void addRd(String line){
        String[] temp = line.split("=>");
        hostsStatus.put(temp[0].trim(),2);
        HostFrAdd.put(temp[0].trim(),new String[]{temp[1]});
    }

    private static void loadHosts(){
        Logger.ilog("Loading subdomains ...");
        try(BufferedReader bf = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/CFGS/Hosts.cfg"))){
            String line;
            while((line = bf.readLine()) != null){
                if(!line.startsWith("#")){
                    if(line.contains("->")) addFr(line);
                    else if (line.contains("=>")) addRd(line);
                    else{
                        HashMap<String, String> temp = new HashMap();
                        Configs.put(line.trim(), temp);
                        hostsStatus.put(line.trim(),0);
                    }
                }
            }
            if (Configs.isEmpty()) {
                HashMap<String, String> temp = new HashMap();
                Configs.put("default", temp);
            }
            loadDirs();
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    private static void loadMain(){
        Logger.ilog("Loading main configs ...");
        JSONBuilder bld = JSONBuilder.newInstance();
        JSONDocument doc = bld.parse(new File(getCWD() + "/CFGS/Zako.cfg"));
        HashMap data = (HashMap) doc.toJava();
        keepAlive = (Boolean) data.get("Keep Alive");
        cache = (Boolean) data.get("Cache Control");
        loadBalancer = (Boolean) data.get("Load Balancer");
        webServer = (Boolean) data.get("Web Server");
        timeout = Integer.parseInt(String.valueOf(data.get("Sockets-Timeout")));
        HashMap cap = (HashMap) data.get("CAPTCHA");
        captcha = (Boolean) cap.get("ON");
        captchaLength = Integer.parseInt(String.valueOf(cap.get("CAPTCHA length")));
        captchaHardness =  Integer.parseInt(String.valueOf(cap.get("CAPTCHA hardness")));
        HashMap ports = (HashMap) data.get("Ports");
        LBPort = Integer.parseInt(String.valueOf(ports.get("Load Balancer")));
        WSPort = Integer.parseInt(String.valueOf(ports.get("Web Server")));
        HashMap sizes = (HashMap) data.get("Sizes");
        if (sizes.get("Post body") != null){
            postBodySize = (int) sizes.get("Post body");
        }
        if (sizes.get("File size") != null){
            fileSize = (int) sizes.get("File size");
        }
        HashMap auth = (HashMap) data.get("HTTP AUTH");
        Server.HttpAuth.Interface.load((Boolean)auth.get("ON"));
    }

    public static void load(){
        loadMain();
        loadHosts();
    }

    public static String getDef(String key){
        String def = "";
        HashMap<String,String> infos = Configs.get("Main");
        if (infos != null){
            def = infos.get(key);
            if (def == null){
                infos = Configs.get("default");
                def = infos.get(key);
            }
        }else{
            infos = Configs.get("default");
            def = infos.get(key);
        }
        return def;
    }

    public static String getMainDir(String host){
        String MainDir = null;
        HashMap<String,String> infos = Configs.get(host);
        if(infos != null){
            MainDir = infos.get("Main");
        }
        return MainDir;
    }

    public static String getLogsDir(String host){
        String LogsDir = null;
        HashMap<String,String> infos = Configs.get(host);
        if(infos != null){
            LogsDir = infos.get("Logs");
            if(LogsDir == null){
                LogsDir = getDef("Logs");
            }
        }else LogsDir = getDef("Logs");
        return LogsDir;
    }

    public static String getCGIDir(String host){
        String CGIDir = null;
        HashMap<String,String> infos = Configs.get(host);
        if(infos != null){
            CGIDir = infos.get("CGI");
            if(CGIDir == null){
                CGIDir = getDef("CGI");
            }
        }else CGIDir = getDef("CGI");
        return CGIDir;
    }

    public static String getUploadDir(String host){
        String UpDir = null;
        HashMap<String,String> infos = Configs.get(host);
        if(infos != null){
            UpDir = infos.get("Up");
            if(UpDir == null){
                UpDir = getDef("Up");
            }
        }else UpDir = getDef("Up");
        return UpDir;
    }

    public static String getCWD(){
        return System.getProperty("user.dir");
    }

    public static boolean isLBOn(){
        return loadBalancer;
    }

    public static boolean isWSOn(){return webServer;}

    public static int getLBPort(){return LBPort;}

    public static int getWSPort(){return WSPort;}

    public static int getHostStatus(String host){
        if (hostsStatus.get(host) != null) return hostsStatus.get(host);
        else return 2;
    }

    public static String[] getForwardAddress(String host){
        return HostFrAdd.get(host);
    }

}

