package Server.Method;

import Server.Reqandres.Request.ServerRequest;
import Server.Reqandres.Request.RequestProcessor;
import Server.Reqandres.Senders.FileSender;
import Server.Utils.Methods;

public class TRACE implements Method{
    @Override
    public int run(ServerRequest req, RequestProcessor reqp) {
        FileSender fs = new FileSender(req.getProt(),200);
        fs.setContentType("message/http");
        fs.sendFile(Methods.GET,req.getCacheFile(),req.out,req.getIP(),req.getID(),req.getHost());
        return 0;
    }
}
