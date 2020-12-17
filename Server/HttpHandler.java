package Server;

import Server.Reqandres.*;
import Server.Utils.*;
import Server.DDOS.Interface;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.Socket;

public class HttpHandler extends Thread{

    private Socket s;

    public HttpHandler(Socket s){
        this.s = s;
        this.start();
    }

    @Override
    public void run(){
        try{
            int id = basicUtils.getID();
            String ip = s.getInetAddress().getHostAddress();
            Logger.glog(s.getRemoteSocketAddress().toString() + " Connected." + "  ; id = " + id, "not available");
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            InputStream in = s.getInputStream();
                if (in.available() < Configs.generalSize) {
                    if (Perms.isIPAllowed(ip)) {
                        if (Interface.checkIP(ip, in.available())) {
                            if (Runtime.getRuntime().freeMemory() > 1000) {
                                Request rq = new Request(out,in, id, s.getInetAddress().getHostAddress());
                                if (rq.stat == 1) new Response(rq, out, id);
                                //rq.tempFile.delete();
                            } else {
                                Logger.glog(s.getRemoteSocketAddress().toString() + " request rejected due to server overload." + "  ; id = " + id, "not available");
                                out.writeBytes(HTMLGen.genOverLoad());
                                out.flush();
                                out.close();
                            }
                        } else {
                            Logger.glog(s.getRemoteSocketAddress().toString() + " request rejected due to DDOS protection." + "  ; id = " + id, "not available");
                            out.writeBytes(HTMLGen.genTooManyRequests(ip));
                            out.flush();
                            out.close();
                        }
                    } else {
                        Logger.glog(s.getRemoteSocketAddress().toString() + " request rejected due to ip ban." + "  ; id = " + id, "not available");
                        out.writeBytes(HTMLGen.genIPBan(ip));
                        out.flush();
                        out.close();
                    }
                } else {
                    Logger.glog(s.getRemoteSocketAddress().toString() + " request rejected due to over size." + "  ; id = " + id, "not available");
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
