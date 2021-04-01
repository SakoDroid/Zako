package Server.Method;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Request.RequestProcessor;
import Server.Reqandres.Senders.Sender;
import Server.Utils.Configs.HTAccess;
import Server.Utils.Configs.Perms;
import Server.Utils.Enums.Methods;

public class OPTIONS implements Method{
    @Override
    public void run(Request req) {
        Sender snd = new Sender(req.getProt(),204);
        snd.setKeepAlive(req.getKeepAlive());
        StringBuilder allowHeader = new StringBuilder();
        for (Methods mt : HTAccess.getInstance().getAllowableMethods(req.getHost())){
            if (mt == Methods.DELETE || mt == Methods.PUT){
                if (Perms.isIPAllowedForPUTAndDelete(req.getIP(),req.getHost()))
                    allowHeader.append(mt).append(", ");
            }else
                allowHeader.append(mt).append(", ");
        }
        snd.addHeader("Allow: " + allowHeader.toString());
        if (req.getHeaders().containsKey("Access-Control-Request-Method"))
            snd.addHeader("Access-Control-Allow-Methods: " + allowHeader.toString());
        if (req.getHeaders().containsKey("Access-Control-Request-Headers")){
            StringBuilder ah = new StringBuilder();
            for (String h : HTAccess.getInstance().getAllowableHeaders(req.getHost()))
                ah.append(h);
            snd.addHeader("Access-Control-Allow-Headers: " + ah);
        }
        String orgHeader = "Access-Control-Allow-Origin: ";
        if (req.getHeaders().containsKey("Origin")){
            String org = req.getHeaders().get("Origin");
            if(HTAccess.getInstance().isOriginAllowed(org,req.getHost()))
                orgHeader += org;
            else
                orgHeader += req.getHost();
        }else
            orgHeader += req.getHost();
        snd.addHeader(orgHeader);
        snd.addHeader("Access-Control-Max-Age: " + HTAccess.getInstance().getAccessControlMaxAge(req.getHost()));
        snd.send(null,req);
    }
}
