package Server.API;

import Server.Captcha.Data;
import Server.Reqandres.Request;
import Server.Reqandres.RequestProcessor;
import Server.Reqandres.Senders.FileSender;

public class CapthaChecker implements API{
    @Override
    public void init(Request req, RequestProcessor reqp) {
        FileSender.setProt(req.getProt());
        FileSender.setStatus(200);
        FileSender.setContentType("text/plain");
        FileSender.send(Data.checkAnswer(req.getIP(),reqp.Body),req.out,req.getIP(),req.getID(),req.getHost());
    }
}
