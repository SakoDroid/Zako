package Server.API;

import Server.Reqandres.HeaderCheck.CacheControl;
import Server.Reqandres.Request.*;
import Server.Reqandres.Senders.*;
import Server.Utils.Configs.Configs;
import Server.Utils.Configs.HTAccess;
import Server.Utils.HashComputer;

import java.io.File;

public class index implements API{

    @Override
    public void init(Request req, RequestProcessor reqp) {
        File ind = new File(Configs.getMainDir(req.getHost()) + "/index.html");
        if (!ind.exists())
            ind = new File(Configs.getMainDir(req.getHost()) + "/index.htm");
        if (ind.exists()){
            CacheControl cc = new CacheControl();
            cc.decide(req.getHeaders(),ind);
            if (cc.getStatus() == 200){
                FileSender fs = new FileSender(req.getProt(),200);
                fs.setKeepAlive(HTAccess.getInstance().isKeepAliveAllowed(req.getHost()) && req.getKeepAlive());
                fs.setContentType("text/html");
                fs.setExtension(".html");
                fs.addHeader("ETag: \"" + new HashComputer(ind).computeHash() + "\"");
                fs.sendFile(ind, req);
            }else
                new QuickSender(req).sendCode(cc.getStatus());
        }else
            new QuickSender(req).sendCode(404);
    }
}
