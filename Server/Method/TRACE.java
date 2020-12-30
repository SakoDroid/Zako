package Server.Method;

import Server.Reqandres.Request;
import Server.Reqandres.RequestProcessor;
import Server.Reqandres.Senders.FileSender;
import Server.Utils.Methods;

public class TRACE implements Method{
    @Override
    public int run(Request req, RequestProcessor reqp) {
        FileSender fs = new FileSender(req.getProt(),200);
        fs.setContentType("message/http");
        fs.sendFile(Methods.GET,req.getCacheFile(),req.out,req.getIP(),req.getID(),req.getHost());
        return 0;
    }
}
