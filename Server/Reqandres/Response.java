package Server.Reqandres;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Request.RequestProcessor;
import Server.Utils.*;
import Server.Utils.ViewCounter.View;

public class Response {

    private final RequestProcessor reqes;
    private final Request request;


    public Response(RequestProcessor rq, Request req){
        this.reqes = rq;
        this.request = req;
        this.handleRes();
    }

    private void handleRes(){
        try{
            Logger.glog("Preparing response to " + request.getIP() + "  ; id = " + request.getID(), request.getHost());
            new View(request.getHost(),request.getIP());
            if (reqes.sit < 300)
                Server.API.Factory.getAPI(request.getURL().getPath()).init(request, reqes);
            else
                basicUtils.sendCode(reqes.sit,request);
        }catch (Exception ex) {
            Logger.logException(ex);
        }
    }
}