package Server.Reqandres.Response;

import Server.Reqandres.Request.ServerRequest;
import Server.Reqandres.Request.RequestProcessor;
import Server.Utils.*;
import Server.Utils.ViewCounter.View;

public class Response {

    private final RequestProcessor reqes;
    private final ServerRequest request;


    public Response(RequestProcessor rq, ServerRequest req){
        this.reqes = rq;
        this.request = req;
        this.handleRes();
    }

    private void handleRes(){
        try{
            Logger.glog("Preparing response to " + request.getIP() + "  ; id = " + request.getID(), request.getHost());
            new View(request.getHost(),request.getIP());
            if (reqes.sit < 300){
                if (this.isClientRequestingProtocolSwitch() &&
                ProtocolSwitcher.isClientsRequestForProtocolSwitchValid(String.valueOf(request.getHeaders().get("Upgrade")), request.getHost() , request.getProt()))
                    new ProtocolSwitcher(this.request);
                else
                    Server.API.Factory.getAPI(request.getPath(), request.getHost()).init(request, reqes);
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
}