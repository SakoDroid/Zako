package Server.Reqandres.Senders;

import Server.Utils.Configs;
import Server.Utils.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.util.Date;
import java.text.SimpleDateFormat;
import Server.Utils.basicUtils;

public class Sender {

    protected static SimpleDateFormat df = new SimpleDateFormat("E, dd MM yyyy HH:mm:ss z");
    protected static String prot;
    protected static String status;
    protected static String contentType;
    protected static String cookie;

    private static String generateResponse(String body){
        String out = prot + " " + status + "\nDate: " + df.format(new Date()) + "\nServer: " + basicUtils.Zako;
        if (body != null) out += "\nContent-Length: " + body.length();
        if (contentType != null) out += "\nContent-Type: " + contentType;
        if (cookie != null) out += "\nSet-Cookie: " + cookie;
        out += "\nConnection : close";
        if (body != null) out += "\n\n" + body;
        return out;
    }

    public static void setProt(String prt){
        prot = prt;
    }

    public static void setStatus(int statusCode){
        status = basicUtils.getStatusCodeComp(statusCode);
    }

    public static void setContentType(String cnt){
        contentType = cnt;
    }

    public static void addCookie(String ck){
        if(cookie == null) cookie = ck;
        else cookie += ";" + ck;
    }

    public static void redirect(String location, DataOutputStream out, String ip, int id, String host){
        Logger.glog("Redirecting " + ip + " to " + location + "  ; id = " + id,host);
        try{
            out.writeBytes(prot + " " + status + "\nDate: " + df.format(new Date()) + "\nServer: " + basicUtils.Zako + "\nLocation: " + location + "\n");
            out.flush();
            out.close();
            basicUtils.delID(id);
            Logger.glog(ip + "'s request redirected to " + location + "!" + "  ; id = " + id,host);
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    public static void sendOptionsMethod(DataOutputStream out,String ip,int id,String host){
        try{
            Logger.glog("Sending back options method response to " + ip + "  ; id = " + id,host);
            out.writeBytes(prot + " 200 OK\nDate: " + df.format(new Date()) + "\nServer: " + basicUtils.Zako + "\nAllow: GET,HEAD,POST,OPTIONS,TRACE,CONNECT,PUT,DELETE\nIPS-Allowed-For-PUT-DELETE: ");
            BufferedReader bf = new BufferedReader(new FileReader(Configs.getCWD() + "/CFGS/IP_List_PUT_DELETE.cfg"));
            String line;
            String ips = "";
            while ((line = bf.readLine()) != null){
                if (!line.startsWith("#")) ips += line + ", ";
            }
            bf.close();
            if (ips.isEmpty()) out.writeBytes("");
            else out.writeBytes(ips.substring(0,ips.length()-1));
            out.flush();
            out.close();
            basicUtils.delID(id);
            Logger.glog(ip + "'s request handled successfully!" + "  ; id = " + id,host);
        }catch (Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    public static void sendConnectMethod(DataOutputStream out,String ip,int id,String host){
        try{
            Logger.glog("Sending back connect method response to " + ip + "  ; id = " + id,host);
            out.writeBytes(prot + " 200 Connection established\nDate: " + df.format(new Date()) + "\nServer: " + basicUtils.Zako);
            out.flush();
            out.close();
            basicUtils.delID(id);
            Logger.glog(ip + "'s request handled successfully!" + "  ; id = " + id,host);
        }catch (Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    public static void send(String data, DataOutputStream out,String ip,int id,String host){
        Logger.glog("Sending data as text/plain to " + ip + "  ; id = " + id,host);
        try{
            out.writeBytes(generateResponse(data));
            out.flush();
            out.close();
            basicUtils.delID(id);
            Logger.glog(ip + "'s request handled successfully!" + "  ; id = " + id,host);
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }
}