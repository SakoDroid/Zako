package Server.Method;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Senders.FileSender;

public class TRACE implements Method{
    @Override
    public void run(Request req) {
        FileSender fs = new FileSender(req.getProt(),200);
        fs.setContentType("message/http");
        fs.sendFile(req.getCacheFile(),req);
    }
}
