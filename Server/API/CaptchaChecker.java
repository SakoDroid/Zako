package Server.API;

import Engines.Captcha.Data;
import Server.Reqandres.Request.Request;
import Server.Reqandres.Request.RequestProcessor;
import Server.Reqandres.Senders.FileSender;
import Server.Utils.basicUtils;

public class CaptchaChecker implements API{
    @Override
    public void init(Request req, RequestProcessor reqp) {
        String ans = new String(basicUtils.toByteArray(reqp.Body));
        System.out.println("ans" + ans);
        FileSender fs = new FileSender(req.getProt(),200);
        fs.setContentType("text/plain");
        fs.setKeepAlive(false);
        fs.send(Data.checkAnswer(req.getIP(),ans,req.getHost()),req);
    }
}
