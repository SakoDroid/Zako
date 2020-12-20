package Server;

import Server.Reqandres.*;
import Server.Utils.*;
import Server.DDOS.Interface;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;

public class HttpHandler extends Thread{

    private Socket s;
    private SSLSocket ss;

    public HttpHandler(Socket s){
        this.s = s;
        this.start();
    }

    public HttpHandler(SSLSocket s){
        this.ss = s;
        this.start();
    }

    @Override
    public void run(){
        try{
            int id = basicUtils.getID();
            String ip;
            String fullip;
            DataOutputStream out;
            InputStream in;
            if (s != null){
                s.setSoTimeout(Configs.timeout);
                ip = s.getInetAddress().getHostAddress();
                fullip = s.getRemoteSocketAddress().toString();
                out = new DataOutputStream(s.getOutputStream());
                in = s.getInputStream();
            }else{
                ss.setSoTimeout(Configs.timeout);
                ip = ss.getInetAddress().getHostAddress();
                fullip = ss.getRemoteSocketAddress().toString();
                out = new DataOutputStream(ss.getOutputStream());
                in = ss.getInputStream();
            }
            Logger.glog(fullip + " Connected." + "  ; id = " + id, "not available");
            if (in.available() < Configs.generalSize) {
                if (Perms.isIPAllowed(ip)) {
                    if (Interface.checkIP(ip, in.available())) {
                        if (Runtime.getRuntime().freeMemory() > 1000) {
                            Request rq = new Request(out, in, id, ip, Configs.isSSLOn());
                            if (rq.stat == 1) new Response(rq, out, id);
                            //rq.tempFile.delete();
                        } else {
                            Logger.glog(fullip + " request rejected due to server overload." + "  ; id = " + id, "not available");
                            out.writeBytes(HTMLGen.genOverLoad());
                            out.flush();
                            out.close();
                        }
                    } else {
                        Logger.glog(fullip + " request rejected due to DDOS protection." + "  ; id = " + id, "not available");
                        out.writeBytes(HTMLGen.genTooManyRequests(ip));
                        out.flush();
                        out.close();
                    }
                } else {
                    Logger.glog(fullip + " request rejected due to ip ban." + "  ; id = " + id, "not available");
                    out.writeBytes(HTMLGen.genIPBan(ip));
                    out.flush();
                    out.close();
                }
            } else {
                Logger.glog(fullip + " request rejected due to over size." + "  ; id = " + id, "not available");
                out.writeBytes(HTMLGen.gen413());
                out.flush();
                out.close();
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
