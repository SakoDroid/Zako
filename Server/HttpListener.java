package Server;

import Server.Reqandres.*;
import Server.Reqandres.Request.Request;
import Server.Reqandres.Request.RequestProcessor;
import Server.Utils.*;
import Engines.DDOS.Interface;
import javax.net.ssl.SSLSocket;
import java.net.Socket;

public class HttpListener extends Thread{

    private final Request req;

    public HttpListener(Socket client){
        this.req = new Request(client);
        this.start();
    }

    public HttpListener(SSLSocket client){
        this.req = new Request(client);
        this.start();
    }

    @Override
    public void run(){
        try{
            Logger.glog(req.getFullip() + " Connected." + "  ; id = " + req.getID(), "not available");
            if (Perms.isIPAllowed(req.getIP())) {
                if (Interface.checkIP(req.getIP())) {
                    if (Runtime.getRuntime().freeMemory() > 1000) {
                        RequestProcessor rq = new RequestProcessor(req);
                        if (rq.stat == 1) new Response(rq, req);
                        //req.getCacheFile().delete();
                    } else {
                        Logger.glog(req.getFullip() + " request rejected due to server overload." + "  ; id = " + req.getID(), "not available");
                        req.out.writeBytes(HTMLGen.genOverLoad());
                        req.out.flush();
                        req.out.close();
                    }
                } else {
                    Logger.glog(req.getFullip() + " request rejected due to DDOS protection." + "  ; id = " + req.getID(), "not available");
                    req.out.writeBytes(HTMLGen.genTooManyRequests(req.getIP()));
                    req.out.flush();
                    req.out.close();
                }
            } else {
                Logger.glog(req.getFullip() + " request rejected due to ip ban." + "  ; id = " + req.getID(), "not available");
                req.out.writeBytes(HTMLGen.genIPBan(req.getIP()));
                req.out.flush();
                req.out.close();
            }
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }
}
