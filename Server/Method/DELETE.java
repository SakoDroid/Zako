package Server.Method;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Senders.QuickSender;
import Server.Reqandres.Senders.Sender;
import Server.Utils.Configs.Configs;
import Server.Utils.Logger;
import Server.Utils.Configs.Perms;

import java.io.File;

public class DELETE implements Method{
    @Override
    public void run(Request req) {
        try{
            if(Perms.isIPAllowedForPUTAndDelete(req.getIP(), req.getHost())){
                File fl = new File(Configs.getMainDir(req.getHost()) + req.getPath());
                if (fl.exists()) {
                    fl.delete();
                    Sender snd = new Sender(req.getProt(),200);
                    snd.send(null,req);
                } else new QuickSender(req).sendCode(404);
            }else new QuickSender(req).sendCode(405);
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }
}
