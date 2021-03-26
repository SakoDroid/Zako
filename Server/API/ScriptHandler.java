package Server.API;

import Engines.CGI;
import Engines.CGIClient.CGIProcess;
import Engines.FCGI.Client.FCGI;
import Server.Reqandres.Request.ServerRequest;
import Server.Utils.*;

import java.io.File;

public class ScriptHandler {

    private final ServerRequest req;
    private final String ext;

    public ScriptHandler(ServerRequest req, String extension){
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
        else basicUtils.sendCode(404,req);
    }
}
