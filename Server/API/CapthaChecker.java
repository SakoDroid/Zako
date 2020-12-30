package Server.API;

import Server.Captcha.Data;
import Server.Reqandres.Request;
import Server.Reqandres.RequestProcessor;
import Server.Reqandres.Senders.FileSender;

public class CapthaChecker implements API{
    @Override
    public void init(Request req, RequestProcessor reqp) {
        FileSender fs = new FileSender(req.getProt(),200);
        fs.setContentType("text/plain");
        fs.send(Data.checkAnswer(req.getIP(),reqp.Body),req.out,req.getIP(),req.getID(),req.getHost());
    }
}
