package Server.Reqandres.Senders;

import Server.Reqandres.Request.Request;
import Server.Utils.Configs.Configs;
import Server.Utils.Logger;

import java.io.File;

public class QuickSender {

    private final Request req;

    public QuickSender(Request req){
        this.req = req;
    }

    public void sendCode(int code){
        FileSender fs = new FileSender(req.getProt(),code);
        fs.setContentType("text/html");
        fs.setExtension(".html");
        fs.sendFile(new File(Configs.getCWD() + "/default_pages/" + code + ".html"),req);
    }

    public void sendBadReq(String reason){
        Logger.glog("Sending back bad request (400) response to " + req.getIP() + ", reason : " + reason + "    ; debug_id = " + req.getID(),req.getHost());
        Sender snd = new Sender(req.getProt(),400);
        snd.setKeepAlive(false);
        snd.setContentType("text/plain");
        snd.send(reason,req);
    }

    public void redirect(int redirectCode,String location){
        if (redirectCode > 299 && redirectCode < 309){
            Logger.glog("Redirecting " + req.getIP() + " to " + location + "  ; id = " + req.getID(),req.getHost());
            Sender snd = new Sender(req.getProt(),redirectCode);
            snd.setKeepAlive(false);
            snd.setContentType("text/plain");
            snd.addHeader("Location: " + location);
            snd.send(null,req);
        }else
            throw new IllegalArgumentException("Response code " + redirectCode + " is not valid for redirection.");
    }
}
