package Server.Method;

import Server.Reqandres.Request.ServerRequest;
import Server.Reqandres.Request.RequestProcessor;
import Server.Reqandres.Senders.Sender;
import Server.Utils.Configs;
import Server.Utils.Logger;
import Server.Utils.Perms;
import Server.Utils.basicUtils;

import java.io.File;

public class DELETE implements Method{
    @Override
    public int run(ServerRequest req, RequestProcessor reqp) {
        try{
            if(Perms.isIPAllowedForPUTAndDelete(req.getIP(), req.getHost())){
                File fl = new File(Configs.getMainDir(req.getHost()) + req.getPath());
                if (fl.exists()) {
                    fl.delete();
                    Sender snd = new Sender(req.getProt(),200);
                    snd.send(null,req);
                } else basicUtils.sendCode(404,req);
            }else basicUtils.sendCode(405,req);
        }catch(Exception ex){
            Logger.logException(ex);
        }
        return 0;
    }
}
