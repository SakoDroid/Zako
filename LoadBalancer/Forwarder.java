package LoadBalancer;

import Server.DDOS.Interface;
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
            String[] sv = Tracker.getServer();
            this.server = new Socket(sv[0],Integer.parseInt(sv[1]));
            this.ip = s.getInetAddress().getHostAddress();
            this.serverip = sv[0];
            this.clientOut = new DataOutputStream(client.getOutputStream());
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
            InputStream clientIn = client.getInputStream();
            if (clientIn.available() > 0){
                if (Perms.isIPAllowed(ip)){
                    if (Interface.checkIP(ip, clientIn.available())) {
                        Logger.glog(ip + " request received. Forwarding to " + serverip,"not available");
                        InputStream serverIn = server.getInputStream();
                        OutputStream serverOut = server.getOutputStream();
                        while (clientIn.available() > 0) {
                            serverOut.write(clientIn.read());
                        }
                        serverOut.flush();
                        int i;
                        while ((i = serverIn.read()) != -1) {
                            clientOut.write(i);
                        }
                        clientOut.flush();
                        client.close();
                        server.close();
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
            }else{
                client.close();
                server.close();
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
        FileSender.setProt("HTTP/1.1");
        FileSender.setContentType("text/html");
        FileSender.setStatus(code);
        FileSender.sendFile(Methods.GET,new File(Configs.getCWD() + "/src/default_pages/" + code + ".html"),clientOut,ip,0,"NA");
    }
}
