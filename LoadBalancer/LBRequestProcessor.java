package LoadBalancer;

import Engines.DDOS.Interface;
import Server.Reqandres.Request.Request;
import Server.Reqandres.Senders.QuickSender;
import Server.Utils.Configs.Configs;
import Server.Utils.Enums.Methods;
import Server.Utils.Proxy;
import Server.Utils.Reader.RequestReader;

public class LBRequestProcessor {

    private final Request req;

    public LBRequestProcessor(Request req){
        this.req = req;
        this.startProcessing();
    }

    private void startProcessing(){
        req.setTimeout(60000);
        RequestReader rr = new RequestReader(req);
        rr.readHeaders();
        rr.finishReading();
        if (!req.getHeaders().isEmpty()){
            if (req.getMethod() != Methods.UNKNOWN){
                if (rr.isProtoFound()) {
                    if (rr.isPathFound()) {
                        if (rr.isHostFound()) {
                            //Proxy is initiated here.
                            new Proxy(Tracker.firstServer, req);
                        } else {
                            new QuickSender(req).sendBadReq("\"Host\" header not found.");
                            if (Server.Utils.Configs.Configs.BRS)
                                Interface.addWarning(req.getIP(), req.getHost());
                        }
                    } else {
                        new QuickSender(req).sendBadReq("No protocol found.");
                        if (Server.Utils.Configs.Configs.BRS)
                            Interface.addWarning(req.getIP(), req.getHost());
                    }
                } else {
                    new QuickSender(req).sendBadReq("No path found.");
                    if (Server.Utils.Configs.Configs.BRS)
                        Interface.addWarning(req.getIP(), req.getHost());
                }
            }else{
                new QuickSender(req).sendBadReq("Invalid method.");
                Interface.addWarning(req.getIP(),req.getHost());
            }
        }else{
            new QuickSender(req).sendBadReq("Empty request");
            if (Configs.BRS)
                Interface.addWarning(req.getIP(),req.getHost());
        }
    }
}
