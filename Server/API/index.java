package Server.API;

import Server.Reqandres.Request.*;
import Server.Reqandres.Senders.*;
import Server.Utils.Configs.Configs;
import Server.Utils.Configs.HTAccess;
import Server.Utils.Headers.HashComputer;
import Server.Utils.Headers.LMGenerator;

import java.io.File;

public class index implements API{

    @Override
    public void init(Request req, RequestProcessor reqp) {
        File ind = new File(Configs.getMainDir(req.getHost()) + "/index.html");
        if (!ind.exists())
            ind = new File(Configs.getMainDir(req.getHost()) + "/index.htm");
        if (ind.exists()){
            FileSender fs = new FileSender(req.getProt(),200);
            fs.setKeepAlive(HTAccess.getInstance().isKeepAliveAllowed(req.getHost()) && req.getKeepAlive());
            fs.setContentType("text/html");
            fs.setExtension(".html");
            if (HTAccess.getInstance().shouldETagBeSent(ind.getAbsolutePath(),req.getHost()))
                fs.addHeader("ETag: \"" + new HashComputer(ind).computeHash() + "\"");
            if (HTAccess.getInstance().shouldLMBeSent(ind.getAbsolutePath(),req.getHost()))
                fs.addHeader("Last-Modified: " + new LMGenerator(ind).generate());
            fs.sendFile(ind, req);
        }else
            new QuickSender(req).sendCode(404);
    }
}
