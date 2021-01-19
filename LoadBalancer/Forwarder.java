package LoadBalancer;

import Engines.DDOS.Interface;
import Server.Reqandres.Senders.FileSender;
import Server.Utils.*;
import Server.Utils.Configs;
import java.net.*;
import java.io.*;

public class Forwarder extends Thread {

    private String serverip;
    private Socket client;
    private Socket server;
    private String ip;
    private DataOutputStream clientOut;

    public Forwarder(Socket s){
        try {
            this.client = s;
            this.client.setSoTimeout(Configs.timeout);
            String[] sv = Tracker.getServer();
            this.server = new Socket(sv[0],Integer.parseInt(sv[1]));
            this.server.setSoTimeout(Configs.timeout);
            this.ip = s.getInetAddress().getHostAddress();
            this.serverip = sv[0];
            clientOut = new DataOutputStream(this.client.getOutputStream());
            this.start();
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    @Override
    public void run(){
        try{
            if (Perms.isIPAllowed(ip)){
                if (Interface.checkIP(ip)) {
                    Logger.glog(ip + " request received. Forwarding to " + serverip,"not available");
                    new Piper(client.getInputStream(),server.getOutputStream(),client,server);
                    new Piper(server.getInputStream(),clientOut,client,server);
                } else {
                    Logger.glog(client.getRemoteSocketAddress().toString() + " request rejected due to DDOS protection.", "not available");
                    clientOut.write(HTMLGen.genTooManyRequests(ip).getBytes());
                    clientOut.flush();
                    clientOut.close();
                }
            }else{
                Logger.glog(client.getRemoteSocketAddress().toString() + " request rejected due to ip ban.", "not available");
                clientOut.write(HTMLGen.genIPBan(ip).getBytes());
                clientOut.flush();
                clientOut.close();
            }
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
            if (ex.toString().contains("Timeout")) this.sendCode(504);
            else this.sendCode(502);
        }
    }

    private void sendCode(int code){
        FileSender fs = new FileSender("HTTP/1.1",code);
        fs.setContentType("text/html");
        fs.sendFile(Methods.GET,new File(Configs.getCWD() + "/default_pages/" + code + ".html"),clientOut,ip,0,"NA");
    }
}
