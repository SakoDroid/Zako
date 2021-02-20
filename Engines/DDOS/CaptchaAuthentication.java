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
        File fl;
        if (System.getProperty("os.name")
                .toLowerCase().contains("windows"))
            fl = new File(System.getProperty("user.dir") + "/default_pages/JSAuth.html");
        else if (System.getProperty("os.name")
                .toLowerCase().contains("linux"))
            fl = new File("/var/lib/default_pages/JSAuth.html");
        else
            fl = new File("/var/lib/default_pages/JSAuth.html");
        FileSender fs = new FileSender("HTTP/1.1",429);
        fs.setContentType("text/html");
        fs.setExtension(".html");
        fs.setKeepAlive(false);
        fs.sendFile(Methods.GET,fl,req.out,req.getIP(),req.getID(),"Not Available");
    }
}
