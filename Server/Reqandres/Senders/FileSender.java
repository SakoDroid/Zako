package Server.Reqandres.Senders;

import Server.Utils.Logger;
import Server.Utils.Methods;
import Server.Utils.basicUtils;

import java.io.*;
import java.util.Date;

public class FileSender extends Sender {


    private static String generateHeaders(long contentLength){
        String out = prot + " " + status + "\nDate: " + df.format(new Date()) + "\nServer: Zako 0.1" +
                "\nContent-Length: " + contentLength + "\nContent-Type: " + contentType + "\nConnection: closed";
        if (cookie != null) out += "\nSet-Cookie: " + cookie;
        out += "\n\n";
        return out;
    }

    public static void sendFile(Methods method, File file, DataOutputStream out, String ip, int id, String host){
        Logger.glog("Sending requested file to " + ip + "   ; file name : " + file.getName() + "  ; id = " + id,host);
        try{
            out.writeBytes(generateHeaders(file.length()));
            if(method != Methods.HEAD){
                FileInputStream in = new FileInputStream(file);
                while (in.available() != 0) {
                    out.write(in.read());
                }
            }
            out.flush();
            out.close();
            Logger.glog(ip + "'s request handled successfully!" + "  ; id = " + id,host);
            basicUtils.delID(id);
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    public static void sendFile(Methods method, byte[] file, DataOutputStream out, String ip, int id, String host){
        Logger.glog("Sending binary file to " + ip + "  ; id = " + id,host);
        System.out.println(file.length);
        try{
            out.writeBytes(generateHeaders(file.length));
            if(method != Methods.HEAD) out.write(file);
            out.flush();
            out.close();
            Logger.glog(ip + "'s request handled successfully!" + "  ; id = " + id,host);
            basicUtils.delID(id);
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