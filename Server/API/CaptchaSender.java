package Server.API;

import Engines.Captcha.Captcha;
import Server.Reqandres.Request.*;
import Server.Reqandres.Senders.FileSender;

public class CaptchaSender implements API{
    @Override
    public void init(Request req) {
        Captcha cp = new Captcha(req.getIP(),req.getHost());
        FileSender fs = new FileSender(req.getProt(),200);
        fs.setContentType("image/png");
        fs.setKeepAlive(false);
        fs.sendFile(cp.image,req);
        cp.image = null;
    }
}
