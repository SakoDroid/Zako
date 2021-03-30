package Server.Method;

import Server.Reqandres.Request.Request;
import Server.Utils.Logger;
import Server.Utils.ViewCounter.View;

public class Def implements Method{

    @Override
    public void run(Request request){
        try{
            Logger.glog("Preparing response to " + request.getIP() + "  ; debug_id = " + request.getID(), request.getHost());
            new View(request.getHost(),request.getIP());
            new Server.API.Factory().getAPI(request.getPath(), request.getHost()).init(request);
        }catch (Exception ex) {
            Logger.logException(ex);
        }
    }
}
