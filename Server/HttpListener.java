package Server;

import LoadBalancer.LBRequestProcessor;
import Server.Reqandres.Request.*;
import Server.Reqandres.Response.Response;
import Server.Utils.*;
import Engines.DDOS.Interface;
import Server.Utils.Configs.Perms;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import java.net.Socket;

public class HttpListener extends Thread{

    private Request req;
    private String hostName;
    private boolean lb;

    public HttpListener(Socket client,String host,boolean loadBalancer){
        if (!SocketsData.getInstance().maxReached(client)){
            this.lb = loadBalancer;
            this.hostName = host;
            this.req = new Request(client);
            this.start();
        }else{
            try {
                client.close();
            }catch (Exception ex){
                Logger.logException(ex);
            }
        }
    }

    public HttpListener(SSLSocket client,String host,boolean loadBalancer){
        if (!SocketsData.getInstance().maxReached(client)){
            this.lb = loadBalancer;
            this.hostName = host;
            this.req = new Request(client);
            try {
                SSLParameters sslp = client.getSSLParameters();
                //sslp.setApplicationProtocols(new String[]{"TLS/1.3","TLS/1.2","TLS/1.1","TLS/1","http/1.1","h2c","h2"});
                sslp.setApplicationProtocols(new String[]{"TLS/1.3", "TLS/1.2", "TLS/1.1", "TLS/1", "http/1.1"});
                client.setSSLParameters(sslp);
                client.startHandshake();
                req.setProt(client.getApplicationProtocol());
                this.start();
            } catch (Exception ex) {
                Logger.logException(ex);
                try {
                    client.close();
                } catch (Exception exc) {
                    Logger.logException(exc);
                }
            }
        }else{
            try {
                client.close();
            }catch (Exception ex){
                Logger.logException(ex);
            }
        }
    }

    @Override
    public void run(){
        try{
            Logger.glog(req.getFullip() + " Connected." + "  ; id = " + req.getID(), hostName);
            if (Perms.isIPAllowed(req.getIP())) {
                if (Interface.checkIP(req.getIP(),req.getHost())) {
                    if (this.lb){
                        new LBRequestProcessor(req);
                    }else{
                        if (Runtime.getRuntime().freeMemory() > 1000) {
                            RequestProcessor rq = new RequestProcessor(req);
                            if (rq.stat == 1) new Response(rq, req);
                            req.clearRequest();
                        } else {
                            Logger.glog(req.getFullip() + " request rejected due to server overload." + "  ; id = " + req.getID(), hostName);
                            req.out.writeBytes(HTMLGen.genOverLoad());
                            req.out.flush();
                            req.out.close();
                        }
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
