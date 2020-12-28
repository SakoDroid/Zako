package Server.Method;

import Server.Reqandres.Request;
import Server.Reqandres.RequestProcessor;
import Server.Reqandres.Senders.FileSender;
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
                FileSender.setProt(req.getProt());
                FileSender.setStatus(201);
                FileSender.send(null,req.out,req.getIP(),req.getID(),req.getHost());
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