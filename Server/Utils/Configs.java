package Server.Utils;

import java.io.*;
import java.util.HashMap;
import java.util.regex.*;

public class Configs {

    private static HashMap<String,HashMap<String,String>> Configs = new HashMap<>();
    private static HashMap<String,Integer> hostsStatus = new HashMap<>();
    private static HashMap<String,String[]> HostFrAdd = new HashMap<>();
    private static int loadBalancer;
    private static int webServer;
    private static int LBPort = 80;
    private static int WSPort = 80;

    public static long generalSize = Long.MAX_VALUE;
    public static long fileSize = Long.MAX_VALUE;
    public static long postBodySize = Long.MAX_VALUE;
    public static int captchaLength = 5;
    public static int captchaHardness = 5;
    public static String MainHost;


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
            if(Host.contains(":")) MainHost = Host.split(":")[0];
            else MainHost = Host;
            HashMap<String, String> temp = new HashMap<>();
            temp.put("Main", MainDir);
            temp.put("Logs", LogsDir);
            temp.put("CGI", CGIDir);
            temp.put("Up", TempUploadDir);
            Configs.put("Main", temp);
        }
    }

    private static void loadDirs(){
        try(FileReader bf = new FileReader(System.getProperty("user.dir") + "/src/CFGS/Dirs.cfg")){
            File tempdir = new File(getCWD() + "/src/Temp");
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

    private static void loadHosts(){
        Logger.ilog("Loading subdomains ...");
        try(BufferedReader bf = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/src/CFGS/Hosts.cfg"))){
            String line;
            while((line = bf.readLine()) != null){
                if(!line.startsWith("#")){
                    if(!line.contains("->")){
                        HashMap<String, String> temp = new HashMap();
                        Configs.put(line.trim(), temp);
                        hostsStatus.put(line.trim(),0);
                    }else addFr(line);
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

    private static void loadSizes(){
        Logger.ilog("Loading Size.cfg ...");
        try(BufferedReader bf = new BufferedReader(new FileReader(getCWD() + "/src/CFGS/Size.cfg"))){
            String line;
            String cfgs = "";
            while((line = bf.readLine()) != null){
                if(!line.startsWith("#")) cfgs += line + "\n";
            }
            Pattern ptn = Pattern.compile("post-body=.*");
            Matcher mc = ptn.matcher(cfgs);
            String pst = "-";
            String fl = "-";
            if (mc.find()) pst = mc.group().replace("post-body=","".replace("\"",""));
            ptn = Pattern.compile("file-size=.*");
            mc = ptn.matcher(cfgs);
            if (mc.find()) fl = mc.group().replace("file-size=","".replace("\"",""));
            if (!pst.equals("-")) postBodySize = Long.parseLong(pst);
            if (!fl.equals("-")) fileSize = Long.parseLong(fl);
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
        try(BufferedReader bf = new BufferedReader(new FileReader(getCWD() + "/src/CFGS/Main.cfg"))){
            String cfgs = "";
            String line;
            while((line = bf.readLine()) != null){
                if(!line.startsWith("#")) cfgs += line + "\n";
            }
            Pattern ptn = Pattern.compile("CAPTCHA-LENGTH=.*");
            Matcher mc = ptn.matcher(cfgs);
            String cpln = "5";
            String cphr = "5";
            String ld = "0";
            String wb = "1";
            String lbp = "80";
            String wsp = "80";
            if (mc.find()) cpln = mc.group().replace("CAPTCHA-LENGTH=","");
            ptn = Pattern.compile("CAPTCHA-HARDNESS=.*");
            mc = ptn.matcher(cfgs);
            if (mc.find()) cphr = mc.group().replace("CAPTCHA-HARDNESS=","");
            ptn = Pattern.compile("Load_Balancer=.*");
            mc = ptn.matcher(cfgs);
            if (mc.find()) ld = mc.group().replace("Load_Balancer=","");
            ptn = Pattern.compile("Web_Server=.*");
            mc = ptn.matcher(cfgs);
            if (mc.find()) wb = mc.group().replace("Web_Server=","");
            ptn = Pattern.compile("Load_Balancer_Port=.*");
            mc = ptn.matcher(cfgs);
            if (mc.find()) lbp = mc.group().replace("Load_Balancer_Port=","");
            ptn = Pattern.compile("Server_Port=.*");
            mc = ptn.matcher(cfgs);
            if (mc.find()) wsp = mc.group().replace("Server_Port=","");
            LBPort = Integer.parseInt(lbp);
            WSPort = Integer.parseInt(wsp);
            webServer = Integer.parseInt(wb);
            loadBalancer = Integer.parseInt(ld);
            captchaLength = Integer.parseInt(cpln);
            captchaHardness = Integer.parseInt(cphr);
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    public static void load(){
        loadHosts();
        loadSizes();
        loadMain();
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
        return loadBalancer == 1;
    }

    public static boolean isWSOn(){return webServer == 1;}

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

