package Engines.FCGI.Client.Core;

import Engines.FCGI.Client.Request.FCGIRequest;
import Engines.FCGI.Client.Response.FCGIResponse;
import Server.Utils.Logger;
import java.net.Socket;
import java.io.*;

public class FCGIEngine {

    private Socket sock;
    private OutputStream out;
    private InputStream in;

    public FCGIEngine(String host, int port, int timeout){
        try{
            this.sock = new Socket(host,port);
            this.sock.setSoTimeout(timeout);
            this.out = this.sock.getOutputStream();
            this.in = this.sock.getInputStream();
        }catch (Exception ex) {
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    public void exec(FCGIRequest request){
        request.send(this.out);
    }

    public FCGIResponse listenForResponse(int reqID){
        FCGIResponse res = new FCGIResponse(this.in,reqID);
        this.closeConnection();
        return res;
    }

    public void closeConnection(){
        try{
            this.sock.close();
        }catch (Exception ex) {
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }
}
