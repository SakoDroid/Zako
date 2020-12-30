package Server.Method;

import Server.Reqandres.Request;
import Server.Reqandres.RequestProcessor;
import Server.Reqandres.Senders.Sender;

public class OPTIONS implements Method{
    @Override
    public int run(Request req, RequestProcessor reqp) {
        Sender snd = new Sender(req.getProt(),200);
        snd.sendOptionsMethod(req.out,req.getIP(),req.getID(),req.getHost());
        return 0;
    }
}
