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
                "\nContent-Length: " + contentLength + "\nContent-Type: " + contentType;
        if (ext != null)
            out += FileTypes.getHeaders(ext);
        if (Double.parseDouble(prot.replace("HTTP/","")) < 2){
            if (keepAlive) out += "\nConnection: keep-alive";
            else out += "\nConnection: close";
        }
        if (cookie != null)
            out += "\nSet-Cookie: " + cookie;
        if (!customHeaders.isEmpty())
            out += "\n" + customHeaders;
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
            Logger.logException(ex);
        }
    }

    public void sendFile(Methods method, byte[] file, DataOutputStream out, String ip, int id, String host){
        Logger.glog("Sending a file (byte[]) to " + ip + "  ; id = " + id,host);
        try{
            out.writeBytes(generateHeaders(file.length));
            if(method != Methods.HEAD)
                out.write(file);
            out.flush();
            Logger.glog(ip + "'s request handled successfully!" + "  ; id = " + id,host);
            basicUtils.delID(id);
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }
}