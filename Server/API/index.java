package Server.API;

import Server.Reqandres.Request;
import Server.Reqandres.RequestProcessor;
import Server.Reqandres.Senders.FileSender;
import Server.Utils.Configs;

import java.io.File;

public class index implements API{

    @Override
    public void init(Request req, RequestProcessor reqp) {
        FileSender.setProt(req.getProt());
        FileSender.setContentType("text/html");
        FileSender.setStatus(200);
        FileSender.sendFile(req.getMethod(), new File(Configs.getMainDir(req.getHost()) + "/index.html"), req.out, req.getIP(), req.getID(), req.getHost());
    }
}
