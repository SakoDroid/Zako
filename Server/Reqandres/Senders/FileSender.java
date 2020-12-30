package Server.Reqandres.Senders;

import Server.Utils.Logger;
import Server.Utils.Methods;
import Server.Utils.basicUtils;

import java.io.*;
import java.util.Date;

public class FileSender extends Sender {


    public FileSender(String prot,int status){
        super(prot, status);
    }

    private String generateHeaders(long contentLength){
        String out = prot + " " + status + "\nDate: " + df.format(new Date()) + "\nServer: " + basicUtils.Zako +
                "\nContent-Length: " + contentLength + "\nContent-Type: " + contentType + "\nConnection: close";
        if (cookie != null) out += "\nSet-Cookie: " + cookie;
        out += "\n\n";
        return out;
    }

    public void sendFile(Methods method, File file, DataOutputStream out, String ip, int id, String host){
        Logger.glog("Sending requested file to " + ip + "   ; file name : " + file.getName() + "  ; id = " + id,host);
        try{
            out.writeBytes(generateHeaders(file.length()));
            if(method != Methods.HEAD){
                FileInputStream in = new FileInputStream(file);
                in.transferTo(out);
                in.close();
            }
            out.flush();
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

    public void sendFile(Methods method, byte[] file, DataOutputStream out, String ip, int id, String host){
        Logger.glog("Sending binary file to " + ip + "  ; id = " + id,host);
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