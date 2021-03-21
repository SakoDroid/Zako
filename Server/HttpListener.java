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
    private final String hostName;
    public HttpListener(Socket client,String host){
        this.hostName = host;
        this.req = new Request(client);
        this.start();
    }

    public HttpListener(SSLSocket client,String host){
        this.hostName= host;
        this.req = new Request(client);
        this.start();
    }

    @Override
    public void run(){
        try{
            Logger.glog(req.getFullip() + " Connected." + "  ; id = " + req.getID(), hostName);
            if (Perms.isIPAllowed(req.getIP())) {
                if (Interface.checkIP(req.getIP(),req.getHost())) {
                    if (Runtime.getRuntime().freeMemory() > 1000) {
                        RequestProcessor rq = new RequestProcessor(req);
                        if (rq.stat == 1) new Response(rq, req);
                        req.getCacheFile().delete();
                    } else {
                        Logger.glog(req.getFullip() + " request rejected due to server overload." + "  ; id = " + req.getID(), hostName);
                        req.out.writeBytes(HTMLGen.genOverLoad());
                        req.out.flush();
                        req.out.close();
                    }
                } else {
                    Logger.glog(req.getFullip() + " request rejected due to DDOS protection." + "  ; id = " + req.getID(), hostName);
                    req.out.writeBytes(HTMLGen.genTooManyRequests(req.getIP()));
                    req.out.flush();
                    req.out.close();
                }
            } else {
                Logger.glog(req.getFullip() + " request rejected due to ip ban." + "  ; id = " + req.getID(), hostName);
                req.out.writeBytes(HTMLGen.genIPBan(req.getIP()));
                req.out.flush();
                req.out.close();
            }
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }
}
