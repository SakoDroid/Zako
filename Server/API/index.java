package Server.API;

import Server.Reqandres.Request;
import Server.Reqandres.RequestProcessor;
import Server.Reqandres.Senders.FileSender;
import Server.Utils.Configs;
import Server.Utils.basicUtils;

import java.io.File;

public class index implements API{

    @Override
    public void init(Request req, RequestProcessor reqp) {
        FileSender.setProt(req.getProt());
        FileSender.setContentType("text/html");
        FileSender.setStatus(200);
        File ind = new File(Configs.getMainDir(req.getHost()) + "/index.html");
        if (ind.exists()) FileSender.sendFile(req.getMethod(), ind, req.out, req.getIP(), req.getID(), req.getHost());
        else {
            ind = new File(Configs.getMainDir(req.getHost()) + "/index.htm");
            if (ind.exists()) FileSender.sendFile(req.getMethod(), ind, req.out, req.getIP(), req.getID(), req.getHost());
            else basicUtils.sendCode(404,req);
        }
    }
}
