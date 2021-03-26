package Server.Method;

import Server.Reqandres.Request.ServerRequest;
import Server.Reqandres.Request.RequestProcessor;
import Server.Reqandres.Senders.Sender;
import Server.Utils.Configs;
import Server.Utils.Logger;
import Server.Utils.Perms;
import Server.Utils.basicUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class PUT implements Method{

    @Override
    public int run(ServerRequest req, RequestProcessor reqp){
        try{
            if(Perms.isIPAllowedForPUTAndDelete(req.getIP(),req.getHost())){
                RandomAccessFile bf = new RandomAccessFile(req.getCacheFile(),"r");
                while(!bf.readLine().isEmpty()){}
                File fl = new File(Configs.getMainDir(req.getHost()) + req.getPath());
                FileOutputStream fos = new FileOutputStream(fl);
                int i;
                while ((i = bf.read()) != -1) {
                    fos.write(i);
                }
                fos.flush();
                fos.close();
                bf.close();
                Sender snd = new Sender(req.getProt(),201);
                snd.send(null,req);
            }else basicUtils.sendCode(405,req);
        }catch(Exception ex){
            Logger.logException(ex);
        }
        return 0;
    }
}
