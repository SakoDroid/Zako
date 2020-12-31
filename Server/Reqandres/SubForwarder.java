package Server.Reqandres;

import Server.Utils.Logger;
import java.net.Socket;
import java.io.*;

public class SubForwarder extends Thread {

    private Socket s;
    private final File temp;
    private final DataOutputStream clientOut;

    public SubForwarder(String[] address, File tempFile,DataOutputStream out,String ip,String Host){
        this.temp = tempFile;
        this.clientOut = out;
        try{
            s = new Socket(address[0], Integer.parseInt(address[1]));
            this.start();
            Logger.glog("Forwarding request for " + Host + " from " + ip + " to " + address[0] + ":" + address[1],Host);
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    @Override
    public void run(){
        try{
            FileInputStream fl = new FileInputStream(temp);
            OutputStream serverOut = s.getOutputStream();
            InputStream serverIn = s.getInputStream();
            fl.transferTo(serverOut);
            serverOut.flush();
            temp.delete();
            serverIn.transferTo(clientOut);
            clientOut.flush();
            s.close();
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
