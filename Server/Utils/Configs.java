package Server.Utils;

import Server.Utils.JSON.*;
import java.io.*;
import java.util.HashMap;
import org.w3c.dom.*;
import javax.xml.parsers.*;

public class Configs {

    //Status 0 means handle, 1 means forward, 2 means redirect.
    private static final HashMap<String,HashMap<String,String>> Dirs = new HashMap<>();
    private static final HashMap<String,Integer> hostsStatus = new HashMap<>();
    private static final HashMap<String,String[]> targets = new HashMap<>();

    private static boolean loadBalancer;
    private static boolean webServer;
    private static int LBPort = 80;
    private static int WSPort = 80;

    public static boolean autoUpdate;
    public static boolean cache;
    public static boolean keepAlive;
    public static long generalSize = Long.MAX_VALUE;
    public static long fileSize = Long.MAX_VALUE;
    public static long postBodySize = Long.MAX_VALUE;
    public static int timeout;

    private Configs(){}

    private static void loadHosts(){
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File("/etc/zako/Hosts.cfg"));
            NodeList nl = doc.getElementsByTagName("Host");
            for (int i = 0 ; i < nl.getLength() ; i++){
                Element host = (Element) nl.item(i);
                String hostName = host.getElementsByTagName("Name").item(0).getTextContent().trim();
                if (hostName.isEmpty())
                    continue;
                String mode =  host.getElementsByTagName("Mode").item(0).getTextContent().trim();
                if (mode.equals("Handle")) {
                    hostsStatus.put(hostName,0);
                    HashMap<String, String> dirs = new HashMap<>();
                    String root = host.getElementsByTagName("RootDir").item(0).getTextContent().trim();
                    String cgi = host.getElementsByTagName("CGIDir").item(0).getTextContent().trim();
                    dirs.put("Root", root);
                    dirs.put("CGI", cgi);
                    dirs.put("Logs", host.getElementsByTagName("LogsDir").item(0).getTextContent().trim());
                    dirs.put("Files", host.getElementsByTagName("TempFileUploadDir").item(0).getTextContent().trim());
                    Perms.addDir(root);
                    Perms.addDir(cgi);
                    Dirs.put(hostName, dirs);
                } else if (mode.equals("Forward")){
                    hostsStatus.put(hostName,1);
                    String target = host.getElementsByTagName("Target").item(0).getTextContent().trim();
                    String[] address = target.split(":");
                    targets.put(hostName,((address.length > 1) ? address : new String[]{address[0],"80"}));
                }else if (mode.equals("Redirect")){
                    hostsStatus.put(hostName,2);
                    targets.put(hostName,new String[]{host.getElementsByTagName("Target").item(0).getTextContent().trim()});
                }
            }
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
        addDefaultSetback();
        checkDirs();
    }

    private static void addDefaultSetback(){
        HashMap<String,String> dirs = new HashMap<>();
        dirs.put("Root","/var/www/html");
        dirs.put("CGI","/var/www/cgi-bin");
        dirs.put("Logs",getCWD() + "/Logs");
        dirs.put("Files","/var/www/files");
        Dirs.put("def",dirs);
    }

    private static void checkDirs(){
        for (String hostName : Dirs.keySet()){
            HashMap<String,String> dirs = Dirs.get(hostName);
            for (String dir : dirs.values()){
                File fl = new File(dir);
                if (!fl.isDirectory())
                    fl.mkdirs();
            }
        }
    }

    private static void loadMain(){
        Logger.ilog("Loading main configs ...");
        JSONBuilder bld = JSONBuilder.newInstance();
        JSONDocument doc = bld.parse(new File("/etc/zako/Zako.cfg"));
        HashMap data = (HashMap) doc.toJava();
        autoUpdate = (Boolean) data.get("CFG Update");
        keepAlive = (Boolean) data.get("Keep Alive");
        cache = (Boolean) data.get("Cache Control");
        loadBalancer = (Boolean) data.get("Load Balancer");
        webServer = (Boolean) data.get("Web Server");
        timeout = Integer.parseInt(String.valueOf(data.get("Sockets-Timeout")));
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
        return Dirs.get("def").get(key);
    }

    public static String getMainDir(String host){
        HashMap<String,String> dirs = Dirs.get(host);
        if (dirs != null)
            return dirs.get("Root");
        else
            return getDef("Root");
    }

    public static String getLogsDir(String host){
        HashMap<String,String> dirs = Dirs.get(host);
        if (dirs != null)
            return dirs.get("Logs");
        else
            return getDef("Logs");
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

    public static int getLBPort(){return LBPort;}

    public static int getWSPort(){return WSPort;}

    public static int getHostStatus(String host){
        if (hostsStatus.get(host) != null) return hostsStatus.get(host);
        else return 3;
    }

    public static String[] getForwardAddress(String host){
        return targets.get(host);
    }

}