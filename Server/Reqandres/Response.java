package Server.Reqandres;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Request.RequestProcessor;
import Server.Utils.*;

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
            if (reqes.sit < 300)
                Server.API.Factory.getAPI(request.Path).init(request, reqes);
            else
                basicUtils.sendCode(reqes.sit,request);
        }catch (Exception ex) {
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }
}