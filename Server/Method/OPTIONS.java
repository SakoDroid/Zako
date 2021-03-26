package Server.Method;

import Server.Reqandres.Request.ServerRequest;
import Server.Reqandres.Request.RequestProcessor;
import Server.Reqandres.Senders.Sender;

public class OPTIONS implements Method{
    @Override
    public int run(ServerRequest req, RequestProcessor reqp) {
        Sender snd = new Sender(req.getProt(),200);
        snd.sendOptionsMethod(req);
        return 0;
    }
}
