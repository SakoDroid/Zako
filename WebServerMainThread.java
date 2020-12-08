import Server.Handler;
import Server.Utils.Configs;
import Server.Utils.Logger;

import java.net.ServerSocket;

public class WebServerMainThread extends Thread{

    public WebServerMainThread(){
        this.start();
    }

    @Override
    public void run(){
        try{
            ServerSocket ss = new ServerSocket(Configs.getWSPort());
            Logger.ilog("Server thread is now running ...");
            while(true) new Handler(ss.accept());
        }catch (Exception ex) {
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }
}
