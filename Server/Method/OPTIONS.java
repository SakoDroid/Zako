package Server.Method;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Request.RequestProcessor;
import Server.Reqandres.Senders.Sender;

public class OPTIONS implements Method{
    @Override
    public void run(Request req) {
        Sender snd = new Sender(req.getProt(),200);
        snd.sendOptionsMethod(req);
    }
}
