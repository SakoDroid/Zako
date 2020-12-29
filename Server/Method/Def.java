package Server.Method;

import Server.Reqandres.Request;
import Server.Reqandres.RequestProcessor;
import Server.Reqandres.SubForwarder;
import Server.Utils.*;

public class Def implements Method{

    @Override
    public int run(Request req, RequestProcessor reqp){
        String Host = req.getHost();
        String Path = req.Path;
        int rtn = 1;
        try{
            if (!basicUtils.LocalHostIP.isEmpty())
                req.setHost(req.getHost().replace(basicUtils.LocalHostIP, Configs.MainHost));
            req.setHost(req.getHost().replace("127.0.0.1", "localhost"));
            int status = Configs.getHostStatus(Host);
            if (status == 0) {
                String[] api = APIConfigs.getAPIAddress(Host + Path);
                if (api == null) {
                    reqp.sit = Perms.isDirPerm(Configs.getMainDir(Host));
                    if (reqp.method == Methods.POST) {
                        reqp.parseBody();
                    } else reqp.bf.close();
                } else {
                    if (api.length > 1) {
                        reqp.bf.close();
                        Logger.glog("request for API " + Host + Path + " received from " + req.getIP() + " .", Host);
                        new SubForwarder(api, req.getCacheFile(), req.out, req.getIP(), Host + Path);
                        rtn = 0;
                    } else {
                        req.Path = api[0];
                    }
                }
            } else if (status == 1) {
                reqp.bf.close();
                Logger.glog("request for " + Host + " received from " + req.Path + " .", Host);
                new SubForwarder(Configs.getForwardAddress(Host), req.getCacheFile(), req.out, req.getIP(), Host);
                rtn = 0;
            } else reqp.sit = 500;
        }catch (Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
        return rtn;
    }
}
