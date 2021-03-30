package Server.Reqandres.HeaderCheck;

import Server.Reqandres.Request.Request;
import Server.Utils.Configs.Configs;
import java.io.File;

public class HeadersChecker {

    private final Request req;

    public HeadersChecker(Request req){
        this.req = req;
        this.startChecking();
    }

    private void startChecking(){
        new ProtocolSwitch(this.req);
        if (req.getResponseCode() == 200){
            Conditionals cc = new Conditionals();
            cc.decide(req.getHeaders(), new File(Configs.getMainDir(req.getHost()) + req.getPath()), req.getMethod());
            req.setResponseCode(cc.getStatus());
            if (req.getResponseCode() == 200) {

            }
        }
    }
}
