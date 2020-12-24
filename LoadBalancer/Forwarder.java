package LoadBalancer;

import Server.DDOS.Interface;
import Server.Reqandres.Senders.FileSender;
import Server.Utils.*;
import Server.Utils.Configs;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;

public class Forwarder extends Thread {

    private String serverip;
    private SocketChannel client;
    private Socket server;
    private String ip;
    private DataOutputStream clientOut;

    public Forwarder(SocketChannel s){
        try {
            this.client = s;
            String[] sv = Tracker.getServer();
            this.server = new Socket(sv[0],Integer.parseInt(sv[1]));
            this.ip = s.socket().getInetAddress().getHostAddress();
            this.serverip = sv[0];
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
                    OutputStream serverOut = server.getOutputStream();
                    this.client.configureBlocking(false);
                    ByteBuffer bf = ByteBuffer.allocate(1024);
                    while (client.read(bf) > 0){
                        bf.flip();
                        serverOut.write(bf.array());
                        bf.clear();
                    }
                    this.client.configureBlocking(true);
                    serverOut.flush();
                    clientOut = new DataOutputStream(this.client.socket().getOutputStream());
                    InputStream serverIn = server.getInputStream();
                    int i;
                    while ((i = serverIn.read()) != -1) {
                        clientOut.write(i);
                    }
                    clientOut.flush();
                    client.close();
                    server.close();
                } else {
                    clientOut = new DataOutputStream(this.client.socket().getOutputStream());
                    Logger.glog(client.socket().getRemoteSocketAddress().toString() + " request rejected due to DDOS protection.", "not available");
                    clientOut.write(HTMLGen.genTooManyRequests(ip).getBytes());
                    clientOut.flush();
                    clientOut.close();
                }
            }else{
                clientOut = new DataOutputStream(this.client.socket().getOutputStream());
                Logger.glog(client.socket().getRemoteSocketAddress().toString() + " request rejected due to ip ban.", "not available");
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
        try{
            clientOut = new DataOutputStream(this.client.socket().getOutputStream());
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
        FileSender.setProt("HTTP/1.1");
        FileSender.setContentType("text/html");
        FileSender.setStatus(code);
        FileSender.sendFile(Methods.GET,new File(Configs.getCWD() + "/default_pages/" + code + ".html"),clientOut,ip,0,"NA");
    }
}
