package Server.Utils;

import Server.Utils.JSON.*;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import org.w3c.dom.*;
import javax.xml.parsers.*;

public class Configs {

    //Status 0 means handle, 1 means forward, 2 means redirect.
    private static final HashMap<String,HashMap<String,String>> Dirs = new HashMap<>();
    private static final HashMap<String,Integer> hostsStatus = new HashMap<>();
    private static final HashMap<String,String[]> targets = new HashMap<>();
    private static final HashMap<String,Boolean> KAS = new HashMap<>();
    private static final HashMap<String,Integer> timeOuts = new HashMap<>();
    private static final HashMap<String,long[]> sizes = new HashMap<>();
    private static final HashMap<String,Integer> ports = new HashMap<>();
    private static final HashSet<String> availableHosts = new HashSet<>();

    private static int LBPort;
    public static final String baseAddress = (System.getProperty("os.name").toLowerCase().contains("linux") ?
            "/etc/zako-web" :
            "CFGS");
    private static boolean loadBalancer;
    private static boolean webServer;
    public static boolean autoUpdate;
    public static boolean BRS = true;

    private Configs(){}

    private static void loadHandle(HashMap data,String hostName){
        try{
            String mode =  String.valueOf(data.get("Handle"));
            switch (mode) {
                case "Handle" -> {
                    hostsStatus.put(hostName, 0);
                    HashMap<String, String> dirs = new HashMap<>();
                    dirs.put("Root", String.valueOf(data.get("RootDir")));
                    dirs.put("CGI", String.valueOf(data.get("CGIDir")));
                    dirs.put("Files", String.valueOf(data.get("TempFileUploadDir")));
                    Dirs.put(hostName, dirs);
                }
                case "Forward" -> {
                    hostsStatus.put(hostName, 1);
                    String target = String.valueOf(data.get("Target"));
                    String[] address = target.split(":");
                    targets.put(hostName, ((address.length > 1) ? address : new String[]{address[0], "80"}));
                }
                case "Redirect" -> {
                    hostsStatus.put(hostName, 2);
                    targets.put(hostName, new String[]{String.valueOf(data.get("Target"))});
                }
            }
        }catch(Exception ex){
            Logger.logException(ex);
        }
        //addDefaultSetback(Dirs.isEmpty());
        checkDirs();
    }

    private static void addDefaultSetback(boolean hostsAdded){
        HashMap<String,String> dirs = new HashMap<>();
        if (hostsAdded){
            dirs.put("Root", "/var/www/html");
            dirs.put("CGI", "/var/www/cgi-bin");
            dirs.put("Files", "/var/www/files");
            Dirs.put("def",dirs);
        }
    }

    private static void checkDirs(){
        File temp = new File(getCWD() + "/Temp");
        if (!temp.isDirectory())
            temp.mkdirs();
        for (String hostName : Dirs.keySet()){
            HashMap<String,String> dirs = Dirs.get(hostName);
            for (String dir : dirs.values()){
                File fl = new File(dir);
                if (!fl.isDirectory())
                    fl.mkdirs();
            }
        }
    }

    public static void loadMain(){
        Logger.ilog("Loading main configs ...");
        File fl = new File(baseAddress + "/Zako.cfg");
        JSONBuilder bld = JSONBuilder.newInstance();
        JSONDocument doc = bld.parse(fl);
        HashMap data = (HashMap) doc.toJava();
        autoUpdate = (Boolean) data.get("CFG Update");
        loadBalancer = (Boolean) data.get("Load Balancer");
        webServer = (Boolean) data.get("Web Server");
        BRS = (Boolean) data.get("BR Sensitivity");
        LBPort = (Integer) data.get("Load Balancer Port");
    }

    public static void loadAHost(File dir){
        HashMap mainData = (HashMap) JSONBuilder.newInstance().parse(new File(dir.getAbsolutePath() + "/Main.cfg")).toJava();
        String name = String.valueOf(mainData.get("Name"));
        loadHandle((HashMap) mainData.get("Reaction"),name);
        KAS.put(name,(Boolean) mainData.get("Keep Alive"));
        ports.put(name,(int) mainData.get("Port"));
        if (KAS.get(name))
            timeOuts.put(name,0);
        else
            timeOuts.put(name,(int) mainData.get("Sockets-Timeout"));
        HashMap szs = (HashMap) mainData.get("Sizes");
        Long pb = (Long) szs.get("Post body");
        Long fs = (Long) szs.get("File size");
        long[] szss = new long[]{Long.MAX_VALUE,Long.MIN_VALUE};
        if (pb != null)
            szss[0] = pb;
        if (fs != null)
            szss[1] = fs;
        sizes.put(name,szss);
        availableHosts.add(name);
    }

    public static String getDef(String key){
        return Dirs.get("def").get(key);
    }

    public static String getMainDir(String host){
        HashMap<String,String> dirs = Dirs.get(host);
        if (dirs != null)
            return dirs.get("Root");
        else
            return getDef("Root");
    }

    public static String getCGIDir(String host){
        HashMap<String,String> dirs = Dirs.get(host);
        if (dirs != null)
            return dirs.get("CGI");
        else
            return getDef("CGI");
    }

    public static String getUploadDir(String host){
        HashMap<String,String> dirs = Dirs.get(host);
        if (dirs != null)
            return dirs.get("Files");
        else
            return getDef("Files");
    }

    public static String getCWD(){
        return System.getProperty("user.dir");
    }

    public static boolean isLBOn(){
        return loadBalancer;
    }

    public static boolean isWSOn(){return webServer;}

    public static HashMap<String,Integer> getPorts() {
        return ports;
    }

    public static int getHostStatus(String host){
        if (hostsStatus.get(host) != null) return hostsStatus.get(host);
        else return 3;
    }

    public static String[] getForwardAddress(String host){
        return targets.get(host);
    }

    public static boolean getKeepAlive(String host){
        return KAS.get(host);
    }

    public static int getTimeOut(String host){
        return timeOuts.get(host);
    }

    public static long getPostBodySize(String host){
        return sizes.get(host)[0];
    }

    public static long getFileSize(String host){
        return sizes.get(host)[1];
    }

    public static boolean isHostAvailable(String host){
        return availableHosts.contains(host);
    }

    public static int getLBPort(){
        return LBPort;
    }

}