package LoadBalancer;

import Server.Utils.Logger;
import java.net.*;
import java.io.*;

public class Reporter extends Thread {

    public Reporter(){
        this.start();
        Logger.ilog("Ram reporter thread is running ...");
    }

    @Override
    public void run(){
        try{
            ServerSocket ss = new ServerSocket(8560);
            while(true){
                Socket s = ss.accept();
                OutputStreamWriter out = new OutputStreamWriter(s.getOutputStream());
                out.write(String.valueOf(Runtime.getRuntime().freeMemory()));
                out.flush();
                out.close();
            }
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }
}
