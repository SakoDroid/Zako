package Server.API;

import Engines.Captcha.Data;
import Server.Reqandres.Request.Request;
import Server.Reqandres.Senders.FileSender;

public class CaptchaChecker implements API{
    @Override
    public void init(Request req) {
        req.convertBody();
        String ans = new String(req.getConvertedBody());
        FileSender fs = new FileSender(req.getProt(),200);
        fs.setContentType("text/plain");
        fs.setKeepAlive(false);
        fs.send(Data.checkAnswer(req.getIP(),ans,req.getHost()),req);
    }
}
