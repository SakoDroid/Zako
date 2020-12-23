package Server;

import Server.Reqandres.*;
import Server.Utils.*;
import Server.DDOS.Interface;
import javax.net.ssl.SSLSocket;
import java.nio.channels.SocketChannel;

public class HttpHandler extends Thread{

    private SocketChannel sh;
    private SSLSocket ss;
    private Request req;

    public HttpHandler(SocketChannel client){
        this.sh = client;
        this.req = new Request(client);
        this.start();
    }

    public HttpHandler(SSLSocket s){
        this.ss = s;
        this.start();
    }

    @Override
    public void run(){
        try{
            Logger.glog(req.getFullip() + " Connected." + "  ; id = " + req.getID(), "not available");
            if (Perms.isIPAllowed(req.getIP())) {
                if (Interface.checkIP(req.getIP(), 0)) {
                    if (Runtime.getRuntime().freeMemory() > 1000) {
                        RequestProcessor rq = new RequestProcessor(req,this.sh);
                        if (rq.stat == 1) new Response(rq, req);
                        //req.getCacheFile().delete();
                    } else {
                        req.genOutputStream();
                        Logger.glog(req.getFullip() + " request rejected due to server overload." + "  ; id = " + req.getID(), "not available");
                        req.getOutputStream().writeBytes(HTMLGen.genOverLoad());
                        req.getOutputStream().flush();
                        req.getOutputStream().close();
                    }
                } else {
                    req.genOutputStream();
                    Logger.glog(req.getFullip() + " request rejected due to DDOS protection." + "  ; id = " + req.getID(), "not available");
                    req.getOutputStream().writeBytes(HTMLGen.genTooManyRequests(req.getIP()));
                    req.getOutputStream().flush();
                    req.getOutputStream().close();
                }
            } else {
                req.genOutputStream();
                Logger.glog(req.getFullip() + " request rejected due to ip ban." + "  ; id = " + req.getID(), "not available");
                req.getOutputStream().writeBytes(HTMLGen.genIPBan(req.getIP()));
                req.getOutputStream().flush();
                req.getOutputStream().close();
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
