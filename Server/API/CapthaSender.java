package Server.API;

import Server.Captcha.Captcha;
import Server.Reqandres.Request;
import Server.Reqandres.RequestProcessor;
import Server.Reqandres.Senders.FileSender;

public class CapthaSender implements API{
    @Override
    public void init(Request req, RequestProcessor reqp) {
        FileSender.setProt(req.getProt());
        FileSender.setContentType("image/png");
        FileSender.setStatus(200);
        FileSender.sendFile(req.getMethod(), new Captcha(req.getIP(),req.getHost()).image,req.out,req.getIP(),req.getID(),req.getHost());
    }
}
