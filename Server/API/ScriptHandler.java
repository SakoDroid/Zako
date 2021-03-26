package Server.API;

import Engines.CGI;
import Engines.CGIClient.CGIProcess;
import Engines.FCGI.Client.FCGI;
import Server.Reqandres.Request.Request;
import Server.Reqandres.Senders.QuickSender;
import Server.Utils.Configs.Configs;
import Server.Utils.Configs.ScriptsConfigs;

import java.io.File;

public class ScriptHandler {

    private final Request req;
    private final String ext;

    public ScriptHandler(Request req, String extension){
        this.req = req;
        this.ext = extension;
    }

    public void process(byte[] body,boolean ka){
        File fl = new File(Configs.getCGIDir(req.getHost()) + req.getPath());
        if (fl.exists()){
            int status = ScriptsConfigs.getHandleMode(req.getHost(),ext);
            CGI client = ((status == 0) ? new CGIProcess(ext,fl,req) : new FCGI(ext,fl,req));
            client.exec(body,ka);
        }
        else
            new QuickSender(req).sendCode(404);
    }
}
