package Server.Reqandres.HeaderCheck;

import Server.Reqandres.Request.Request;
import Server.Utils.Configs.HTAccess;
import java.util.regex.*;

public class CORS {

    private final Request req;

    public CORS(Request req){
        this.req = req;
        this.process();
    }

    private void process(){
        String origin = req.getHeaders().get("Origin");
        if (origin != null){
            Pattern ptn = Pattern.compile("\\w+:\\\\\\\\");
            Matcher mc = ptn.matcher(origin);
            if (mc.find()){
                String org = mc.group().split(":",2)[0];
                if (HTAccess.getInstance().isMethodAllowed(req.getMethod(),req.getHost())) {
                    if (!HTAccess.getInstance().isOriginAllowed(org, req.getHost())) {
                        req.setResponseCode(400);
                        req.setErrorReason("Origin not allowed.");
                    }
                }else
                    req.setResponseCode(405);
            }
        }
    }
}
