package Server.Reqandres.Response;

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
            if (reqes.sit < 300){
                if (this.isClientRequestingProtocolSwitch()){
                    if (this.isClientsRequestForProtocolSwitchValid())
                        new ProtocolSwitcher(this.request);
                }else
                    Server.API.Factory.getAPI(request.Path, request.getHost()).init(request, reqes);
            }
            else
                basicUtils.sendCode(reqes.sit,request);
        }catch (Exception ex) {
            Logger.logException(ex);
        }
    }

    private boolean isClientRequestingProtocolSwitch(){
        Object cnc = request.getHeaders().get("Connection");
        if (cnc != null){
            String connection = String.valueOf(cnc);
            if (connection.equals("upgrade")){
                Object up = request.getHeaders().get("Upgrade");
                return up != null;
            }
        }
        return false;
    }

    private boolean isClientsRequestForProtocolSwitchValid(){
        Object up = request.getHeaders().get("Upgrade");
        if (up != null){
            if (request.getProt().equals("HTTP/1.1"))
                return !String.valueOf(up).equalsIgnoreCase("http/1.1");
            else if (request.getProt().equals("HTTP/2"))
                return !String.valueOf(up).equalsIgnoreCase("h2") && !String.valueOf(up).equalsIgnoreCase("h2c") &&
                        !String.valueOf(up).equalsIgnoreCase("http/2");
        }
        return false;
    }
}