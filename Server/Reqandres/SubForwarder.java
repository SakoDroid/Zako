package Server.Reqandres;

import Server.Utils.Logger;
import java.net.Socket;
import java.io.*;

public class SubForwarder extends Thread {

    private Socket s;
    private File temp;
    private DataOutputStream clientOut;

    public SubForwarder(String[] address, File tempFile,DataOutputStream out,String ip,String Host){
        this.temp = tempFile;
        this.clientOut = out;
        try{
            s = new Socket(address[0], Integer.parseInt(address[1]));
            this.start();
            Logger.glog("Forwarding request fot " + Host + " from " + ip + " to " + address[0] + ":" + address[1],Host);
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
            int i;
            while((i = fl.read()) != -1){
                serverOut.write(i);
            }
            serverOut.flush();
            temp.delete();
            while((i = serverIn.read()) != -1){
                clientOut.write(i);
            }
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
