package Server.Reqandres.Senders;

import Server.Utils.*;
import java.io.*;
import java.util.Date;

public class FileSender extends Sender {

    private String ext;

    public FileSender(String prot,int status){
        super(prot, status);
    }

    private String generateHeaders(long contentLength){
        String out = prot + " " + status + "\nDate: " + df.format(new Date()) + "\nServer: " + basicUtils.Zako +
                "\nContent-Length: " + contentLength + "\nContent-Type: " + contentType + "\nCache-Control: ";
        if (ext != null){
            out += FileTypes.getAge(ext);
        }else out += "no-store";
        if (keepAlive) out += "\nConnection: keep-alive";
        else out += "\nConnection: close";
        if (cookie != null) out += "\nSet-Cookie: " + cookie;
        out += "\n\n";
        return out;
    }

    public void setExtension(String ext){
        this.ext = ext;
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