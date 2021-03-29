package Server.Reqandres.Senders;

import Server.Reqandres.Request.Request;
import Server.Utils.*;
import Server.Utils.Configs.FileTypes;
import Server.Utils.Enums.Methods;
import java.io.*;
import java.util.Date;

public class FileSender extends Sender {

    private String ext;

    public FileSender(String prot,int status){
        super(prot, status);
    }

    private String generateHeaders(long contentLength,String host,Request req){
        String out = prot + " " + status + "\nDate: " + df.format(new Date()) + "\nServer: " + basicUtils.Zako +
                "\nContent-Length: " + contentLength + "\nContent-Type: " + contentType + "\nStatus: " + this.status;
        if (ext != null)
            out += FileTypes.getHeaders(ext,host);
        if (!req.getHost().equals("HTTP/2"))
            out += this.getConnectionHeader(req);
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

    public void sendFile(File file, Request req){
        try{
            if (!req.getSocket().isClosed()){
                Logger.glog("Sending requested file to " + req.getIP() + "   ; file name : " + file.getName() + "  ; debug_id = " + req.getID(), req.getHost());
                req.out.writeBytes(generateHeaders(file.length(), req.getHost(), req));
                if (req.getMethod() != Methods.HEAD) {
                    FileInputStream in = new FileInputStream(file);
                    in.transferTo(req.out);
                    in.close();
                }
                if (!this.keepAlive) {
                    req.out.flush();
                    req.out.close();
                }
                Logger.glog(req.getIP() + "'s request handled successfully!" + "  ; debug_id = " + req.getID(), req.getHost());
            }else
                Logger.glog("Connection closed with " + req.getIP() + " without sending response.; debug_id = " + req.getID(),req.getHost());
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    public void sendFile(byte[] file, Request req){
        try{
            if (!req.getSocket().isClosed()){
                Logger.glog("Sending a file (stored in RAM) to " + req.getIP() + "  ; debug_id = " + req.getID(), req.getHost());
                req.out.writeBytes(generateHeaders(file.length, req.getHost(), req));
                if (req.getMethod() != Methods.HEAD)
                    req.out.write(file);
                if (!this.keepAlive) {
                    req.out.flush();
                    req.out.close();
                }
                Logger.glog(req.getIP() + "'s request handled successfully!" + "  ; debug_id = " + req.getID(), req.getHost());
            }else
                Logger.glog("Connection closed with " + req.getIP() + " without sending response.; debug_id = " + req.getID(),req.getHost());
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }
}