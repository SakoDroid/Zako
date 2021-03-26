package Server.API;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Request.RequestProcessor;
import Server.Reqandres.Senders.FileSender;
import Server.Reqandres.Senders.QuickSender;
import Server.Utils.Configs.Configs;

import java.io.File;

public class index implements API{

    @Override
    public void init(Request req, RequestProcessor reqp) {
        File ind = new File(Configs.getMainDir(req.getHost()) + "/index.html");
        if (!ind.exists()){
            ind = new File(Configs.getMainDir(req.getHost()) + "/index.htm");
        }
        if (ind.exists()){
            FileSender fs = new FileSender(req.getProt(),200);
            fs.setKeepAlive(Configs.getKeepAlive(req.getHost()) && reqp.KA);
            fs.setContentType("text/html");
            fs.setExtension(".html");
            fs.sendFile(ind, req);
        }else new QuickSender(req).sendCode(404);;
    }
}
