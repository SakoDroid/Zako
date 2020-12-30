package LoadBalancer;

import Server.DDOS.Interface;
import Server.Reqandres.Senders.FileSender;
import Server.Utils.*;
import Server.Utils.Configs;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    read(client.getInputStream(),new DataOutputStream(server.getOutputStream()));
                    InputStream serverIn = server.getInputStream();
                    serverIn.transferTo(clientOut);
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

    private void read(InputStream in,DataOutputStream out){
        try{
            int len = 0;
            String line = this.readLine(in);
            out.writeBytes(line + "\r\n");
            while ((line = this.readLine(in)) != null) {
                if (!line.isEmpty()) {
                    out.writeBytes((line + "\n\r"));
                    if (line.contains("Content-Length")){
                        len = Integer.parseInt(line.split(":",2)[1].trim());
                    }
                }else break;
            }
            out.writeBytes("\n\r");
            if (len != 0){
                out.write(in.readNBytes(len+1));
            }
            out.flush();
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    private String readLine(InputStream in){
        StringBuilder sb = new StringBuilder();
        int i;
        try{
            i = in.read();
            if (i == -1) return null;
            while (i != 13){
                if (i != 10 ) sb.append((char)i);
                i = in.read();
                if (i == -1) break;
            }
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
        return sb.toString();
    }

    private void sendCode(int code){
        FileSender fs = new FileSender("HTTP/1.1",code);
        fs.setContentType("text/html");
        fs.sendFile(Methods.GET,new File(Configs.getCWD() + "/default_pages/" + code + ".html"),clientOut,ip,0,"NA");
    }
}
