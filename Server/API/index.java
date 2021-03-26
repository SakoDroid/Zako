package Server.API;

import Server.Reqandres.Request.ServerRequest;
import Server.Reqandres.Request.RequestProcessor;
import Server.Reqandres.Senders.FileSender;
import Server.Utils.Configs;
import Server.Utils.basicUtils;

import java.io.File;

public class index implements API{

    @Override
    public void init(ServerRequest req, RequestProcessor reqp) {
        File ind = new File(Configs.getMainDir(req.getHost()) + "/index.html");
        if (!ind.exists()){
            ind = new File(Configs.getMainDir(req.getHost()) + "/index.htm");
        }
        if (ind.exists()){
            FileSender fs = new FileSender(req.getProt(),200);
            fs.setKeepAlive(Configs.getKeepAlive(req.getHost()) && reqp.KA);
            fs.setContentType("text/html");
            fs.setExtension(".html");
            fs.sendFile(req.getMethod(), ind, req.out, req.getIP(), req.getID(), req.getHost());
        }else basicUtils.sendCode(404,req);
    }
}
