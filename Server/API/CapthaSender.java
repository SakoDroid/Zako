package Server.API;

import Engines.Captcha.Captcha;
import Server.Reqandres.Request.Request;
import Server.Reqandres.Request.RequestProcessor;
import Server.Reqandres.Senders.FileSender;

public class CapthaSender implements API{
    @Override
    public void init(Request req, RequestProcessor reqp) {
        FileSender fs = new FileSender(req.getProt(),200);
        fs.setContentType("image/png");
        fs.setKeepAlive(false);
        fs.sendFile(req.getMethod(), new Captcha(req.getIP(),req.getHost()).image,req.out,req.getIP(),req.getID(),req.getHost());
    }
}
