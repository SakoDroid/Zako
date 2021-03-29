package Server.Method;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Request.RequestProcessor;
import Server.Reqandres.Senders.QuickSender;
import Server.Reqandres.Senders.Sender;
import Server.Utils.Configs.Configs;
import Server.Utils.Logger;
import Server.Utils.Configs.Perms;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class PUT implements Method{

    @Override
    public int run(Request req, RequestProcessor reqp){
        try{
            if(Perms.isIPAllowedForPUTAndDelete(req.getIP(),req.getHost())){
                File fl = new File(Configs.getMainDir(req.getHost()) + req.getPath());
                RandomAccessFile bf = new RandomAccessFile(req.getCacheFile(),"r");
                while(!bf.readLine().isEmpty()){}
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
            }else
                new QuickSender(req).sendCode(405);
        }catch(Exception ex){
            Logger.logException(ex);
        }
        return 0;
    }
}
