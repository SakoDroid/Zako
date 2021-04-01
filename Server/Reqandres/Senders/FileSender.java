package Server.Reqandres.Senders;

import Server.Reqandres.Request.Request;
import Server.Utils.*;
import Server.Utils.Enums.Methods;
import java.io.*;

public class FileSender extends Sender {

    public FileSender(String prot,int status){
        super(prot, status);
    }

    private String generateHeaders(Request req,String filePath,long bodyLength) {
        new HeaderGenerator(this.ext,filePath,req).generate(this.headers,bodyLength);
        return super.turnHeadersIntoString(req.getProt());
    }

    public void sendFile(File file, Request req){
        try{
            if (!req.getSocket().isClosed()){
                Logger.glog("Sending requested file to " + req.getIP() + "   ; file name : " + file.getName() + "  ; debug_id = " + req.getID(), req.getHost());
                req.out.writeBytes(generateHeaders(req,file.getAbsolutePath(),file.length()));
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
                req.out.writeBytes(generateHeaders(req,file.length));
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