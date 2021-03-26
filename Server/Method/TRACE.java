package Server.Method;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Request.RequestProcessor;
import Server.Reqandres.Senders.FileSender;

public class TRACE implements Method{
    @Override
    public int run(Request req, RequestProcessor reqp) {
        FileSender fs = new FileSender(req.getProt(),200);
        fs.setContentType("message/http");
        fs.sendFile(req.getCacheFile(),req);
        return 0;
    }
}
