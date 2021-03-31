package Server;

import LoadBalancer.LBRequestProcessor;
import Server.Reqandres.Request.*;
import Server.Utils.*;
import Engines.DDOS.Interface;
import Server.Utils.Configs.Perms;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import java.net.Socket;

public class HttpListener extends Thread{

    private final Request req;
    private final String hostName;
    private final boolean lb;

    public HttpListener(Socket client,String host,boolean loadBalancer){
        this.lb = loadBalancer;
        this.hostName = host;
        this.req = new Request(client);
        if (!SocketsData.getInstance().maxReached(client))
            this.start();
        else{
            SocketsData.getInstance().removeEntry(client);
            try {
                client.close();
            }catch (Exception ex){
                Logger.logException(ex);
            }
        }
    }

    public HttpListener(SSLSocket client,String host,boolean loadBalancer){
        this.lb = loadBalancer;
        this.hostName = host;
        this.req = new Request(client);
        try {
            SSLParameters sslp = client.getSSLParameters();
            //sslp.setApplicationProtocols(new String[]{"TLS/1.3","TLS/1.2","TLS/1.1","TLS/1","http/1.1","h2"});
            sslp.setApplicationProtocols(new String[]{"TLS/1.3", "TLS/1.2", "TLS/1.1", "TLS/1", "http/1.1"});
            client.setSSLParameters(sslp);
            client.startHandshake();
            req.setProt(client.getApplicationProtocol());
        } catch (Exception ex) {
            Logger.logException(ex);
            try {
                client.close();
            } catch (Exception exc) {
                Logger.logException(exc);
            }
        }
        if (!SocketsData.getInstance().maxReached(client))
            this.start();
        else{
            SocketsData.getInstance().removeEntry(client);
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
            Logger.glog(req.getFullIp() + " Connected." + "  ; id = " + req.getID(), hostName);
            if (Perms.isIPAllowed(req.getIP())) {
                if (Interface.checkIP(req.getIP(),req.getHost())) {
                    if (this.lb){
                        new LBRequestProcessor(req);
                    }else{
                        if (Runtime.getRuntime().freeMemory() > 1000) {
                            new RequestProcessor(req);
                            req.clearRequest();
                        } else {
                            Logger.glog(req.getFullIp() + " request rejected due to server overload." + "  ; id = " + req.getID(), hostName);
                            req.out.writeBytes(HTMLGen.genOverLoad());
                            req.out.flush();
                            req.out.close();
                        }
                    }
                } else {
                    Logger.glog(req.getFullIp() + " request rejected due to DDOS protection." + "  ; id = " + req.getID(), hostName);
                    req.out.writeBytes(HTMLGen.genTooManyRequests(req.getIP()));
                    req.out.flush();
                    req.out.close();
                }
            } else {
                Logger.glog(req.getFullIp() + " request rejected due to ip ban." + "  ; id = " + req.getID(), hostName);
                req.out.writeBytes(HTMLGen.genIPBan(req.getIP()));
                req.out.flush();
                req.out.close();
            }
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }
}
