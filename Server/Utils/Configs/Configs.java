package Server.Utils.Configs;

import Server.Utils.JSON.*;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import Server.Utils.Logger;

public class Configs {

    //Status 0 means handle, 1 means forward, 2 means redirect.

    private static final HashMap<String,Config> configs = new HashMap<>();
    private static final HashMap<String,Integer> ports = new HashMap<>();
    private static final HashSet<String> availableHosts = new HashSet<>();
    public static final String baseAddress = (System.getProperty("os.name").toLowerCase().contains("linux") ?
            "/etc/zako-web" :
            "CFGS");
    private static boolean webServer;
    public static boolean autoUpdate;
    public static boolean BRS = true;

    private Configs(){}

    public static void loadMain(){
        Logger.ilog("Loading main configs ...");
        File fl = new File(baseAddress + "/Zako.conf");
        JSONBuilder bld = JSONBuilder.newInstance();
        JSONDocument doc = bld.parse(fl);
        HashMap data = (HashMap) doc.toJava();
        autoUpdate = (Boolean) data.get("CFG Update");
        webServer = (Boolean) data.get("Web Server");
        BRS = (Boolean) data.get("BR Sensitivity");
        Server.Utils.ViewCounter.Controller.load(availableHosts,(Long) data.get("View update frequency"));
    }

    public static void loadAHost(File dir){
        configs.put(dir.getName(),new Config(dir));
    }

    public static String getDef(String key){
        return configs.get("default").dirs.get(key);
    }

    public static String getMainDir(String host){
        Config hst = configs.get(host);
        if (hst != null)
            return hst.dirs.get("Root");
        else
            return getDef("Root");
    }

    public static String getCGIDir(String host){
        Config hst = configs.get(host);
        if (hst != null)
            return hst.dirs.get("CGI");
        else
            return getDef("CGI");
    }

    public static String getUploadDir(String host){
        Config hst = configs.get(host);
        if (hst != null)
            return hst.dirs.get("Files");
        else
            return getDef("Files");
    }

    public static String getCWD(){
        return System.getProperty("user.dir");
    }

    public static boolean isWSOn(){return webServer;}

    public static HashMap<String,Integer> getPorts() {
        return ports;
    }

    public static int getHostStatus(String host){
        if (configs.get(host) != null)
            return configs.get(host).hostStatus;
        else return 3;
    }

    public static String[] getForwardAddress(String host){
        return configs.get(host).target;
    }

    public static int getTimeOut(String host){
        return configs.get(host).timeOut;
    }

    public static long getPostBodySize(String host){
        return configs.get(host).pb;
    }

    public static long getFileSize(String host){
        return configs.get(host).fs;
    }

    public static boolean isHostAvailable(String host){
        return availableHosts.contains(host);
    }

    public static boolean isVCOn(String host){
        return configs.get(host).vc;
    }


    private static class Config{

        private final HashMap<String,String> dirs = new HashMap<>();
        private int hostStatus;
        private String[] target;
        private int timeOut;
        private long pb;
        private long fs;
        private boolean vc;

        public Config(File fl){
            this.load(fl);
        }

        private void load(File fl){
            HashMap mainData = (HashMap) JSONBuilder.newInstance().parse(new File(fl.getAbsolutePath() + "/Main.conf")).toJava();
            String name = String.valueOf(mainData.get("Name"));
            loadHandle((HashMap) mainData.get("Reaction"));
            ports.put(name,(int)(long) mainData.get("Port"));
            timeOut = (int)(long) mainData.get("Sockets-Timeout");
            HashMap szs = (HashMap) mainData.get("Sizes");
            Object p = szs.get("Post body");
            Object f = szs.get("File size");
            if (p == null)
                pb = Long.MAX_VALUE;
            else
                pb = (long) p;
            if (f == null)
                fs = Long.MAX_VALUE;
            else
                fs = (long) f;
            vc = (Boolean) mainData.get("View counter");
            availableHosts.add(name);
        }

        private void loadHandle(HashMap data){
            try{
                String mode =  String.valueOf(data.get("Handle"));
                switch (mode) {
                    case "Handle" -> {
                        hostStatus = 0;
                        dirs.put("Root", String.valueOf(data.get("RootDir")));
                        dirs.put("CGI", String.valueOf(data.get("CGIDir")));
                        dirs.put("Files", String.valueOf(data.get("TempFileUploadDir")));
                    }
                    case "Forward" -> {
                        hostStatus = 1;
                        String tr = String.valueOf(data.get("Target"));
                        String[] address = tr.split(":");
                        target = ((address.length > 1) ? address : new String[]{address[0], "80"});
                    }
                    case "Redirect" -> {
                        hostStatus = 2;
                        target = new String[]{String.valueOf(data.get("Target"))};
                    }
                }
            }catch(Exception ex){
                Logger.logException(ex);
            }
            checkDirs();
        }

        private void checkDirs(){
            File cache = new File(getCWD() + "/Cache");
            if (!cache.isDirectory())
                cache.mkdirs();
            File temp = new File(getCWD() + "/Cache/Temp");
            if (!temp.isDirectory())
                temp.mkdirs();
            File comp = new File(getCWD() + "/Cache/Compressed");
            if (!comp.isDirectory())
                comp.mkdirs();
            for (String key : dirs.keySet()){
                File fl = new File(dirs.get(key));
                if (!fl.isDirectory())
                    fl.mkdirs();

            }
        }
    }
}