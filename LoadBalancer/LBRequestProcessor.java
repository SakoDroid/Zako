package LoadBalancer;

import Server.Reqandres.Request.Request;
import Server.Utils.Proxy;

public class LBRequestProcessor {

    private final Request req;

    public LBRequestProcessor(Request req){
        this.req = req;
        this.startProcessing();
    }

    private void startProcessing(){
        req.setTimeout(60000);
        new Proxy(Tracker.firstServer,false,req);
    }
}
