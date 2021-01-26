package Server.Method;

import Server.Reqandres.Request.Request;
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
    public int run(Request req, RequestProcessor reqp){
        try{
            if(Perms.isIPAllowedForPUTAndDelete(req.getIP())){
                RandomAccessFile bf = new RandomAccessFile(req.getCacheFile(),"r");
                while(!bf.readLine().isEmpty()){}
                File fl = new File(Configs.getMainDir(req.getHost()) + req.Path);
                FileOutputStream fos = new FileOutputStream(fl);
                int i;
                while ((i = bf.read()) != -1) {
                    fos.write(i);
                }
                fos.flush();
                fos.close();
                bf.close();
                Sender snd = new Sender(req.getProt(),201);
                snd.send(null,req.out,req.getIP(),req.getID(),req.getHost());
            }else basicUtils.sendCode(405,req);
        }catch(Exception ex){
            Logger.logException(ex);
        }
        return 0;
    }
}
