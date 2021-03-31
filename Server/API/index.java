package Server.API;

import Server.Reqandres.Request.*;
import Server.Reqandres.Senders.*;
import Server.Utils.Configs.Configs;
import java.io.File;

public class index implements API{

    @Override
    public void init(Request req) {
        File ind = new File(Configs.getMainDir(req.getHost()) + "/index.html");
        if (!ind.exists())
            ind = new File(Configs.getMainDir(req.getHost()) + "/index.htm");
        new QuickSender(req).sendFile(ind,".html");
    }
}
