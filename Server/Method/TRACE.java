package Server.Method;

import Server.Reqandres.Request;
import Server.Reqandres.RequestProcessor;
import Server.Reqandres.Senders.FileSender;
import Server.Utils.Methods;

public class TRACE implements Method{
    @Override
    public int run(Request req, RequestProcessor reqp) {
        FileSender.setProt(req.getProt());
        FileSender.setContentType("message/http");
        FileSender.setStatus(200);
        FileSender.sendFile(Methods.GET,req.getCacheFile(),req.out,req.getIP(),req.getID(),req.getHost());
        return 0;
    }
}
