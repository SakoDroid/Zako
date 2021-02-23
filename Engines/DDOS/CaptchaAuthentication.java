package Engines.DDOS;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Senders.FileSender;
import Server.Utils.Methods;

import java.io.File;

public class CaptchaAuthentication {

    private final Request req;

    public CaptchaAuthentication(Request rq){
        req = rq;
    }

    public void sendAuthPage(){
        FileSender fs = new FileSender("HTTP/1.1",200);
        fs.setContentType("text/html");
        fs.setExtension(".html");
        fs.setKeepAlive(false);
        fs.sendFile(Methods.GET,new File(System.getProperty("user.dir") + "/default_pages/JSAuth.html")
                ,req.out,req.getIP(),req.getID(),"Not Available");
    }
}
