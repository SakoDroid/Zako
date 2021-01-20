package Server.Method;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Request.RequestProcessor;
import Server.Reqandres.Senders.Sender;
import Server.Utils.Configs;
import Server.Utils.Logger;
import Server.Utils.Perms;
import Server.Utils.basicUtils;

import java.io.File;

public class DELETE implements Method{
    @Override
    public int run(Request req, RequestProcessor reqp) {
        try{
            if(Perms.isIPAllowedForPUTAndDelete(req.getIP())){
                File fl = new File(Configs.getMainDir(req.getHost()) + req.Path);
                if (fl.exists()) {
                    fl.delete();
                    Sender snd = new Sender(req.getProt(),200);
                    snd.send(null,req.out,req.getIP(),req.getID(),req.getHost());
                } else basicUtils.sendCode(404,req);
            }else basicUtils.sendCode(405,req);
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
        return 0;
    }
}
