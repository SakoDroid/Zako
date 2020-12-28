package Server.Method;

import Server.Reqandres.Request;
import Server.Reqandres.RequestProcessor;
import Server.Reqandres.Senders.FileSender;

public class OPTIONS implements Method{
    @Override
    public int run(Request req, RequestProcessor reqp) {
        FileSender.setProt(req.getProt());
        FileSender.sendOptionsMethod(req.out,req.getIP(),req.getID(),req.getHost());
        return 0;
    }
}
