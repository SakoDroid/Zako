package Server.Reqandres.Senders;

import Server.Reqandres.Request.Request;
import Server.Utils.Configs.Configs;

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
        Sender snd = new Sender(req.getProt(),400);
        snd.setKeepAlive(false);
        snd.setContentType("text/plain");
        snd.send(reason,req);
    }
}
