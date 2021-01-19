package Server.Reqandres;

import Server.Utils.Logger;
import java.net.Socket;
import java.io.*;

public class SubForwarder{

    public SubForwarder(String[] address, File tempFile,DataOutputStream out,String ip,String Host){
        try{
            Socket s = new Socket(address[0], Integer.parseInt(address[1]));
            new Piper(tempFile,s.getOutputStream(),s);
            new Piper(s.getInputStream(),out,s);
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

    private class Piper extends Thread {

        private File fl;
        private final OutputStream out;
        private InputStream in;
        private final Socket connection;

        public Piper (File cachedFile, OutputStream os, Socket s){
            this.fl = cachedFile;
            this.out = os;
            this.connection = s;
            this.start();
        }

        public Piper (InputStream is, OutputStream os, Socket s){
            this.in = is;
            this.out = os;
            this.connection = s;
            this.start();
        }

        @Override
        public void run(){
            try{
                if (fl != null){
                    FileInputStream fis = new FileInputStream(fl);
                    fis.transferTo(out);
                }else
                    in.transferTo(out);
                out.flush();
            }catch(Exception ex){
                try {
                    connection.close();
                }catch (Exception ignored){}
                String t = "";
                for (StackTraceElement a : ex.getStackTrace()){
                    t += a.toString() + " ;; ";
                }
                t += ex.toString();
                Logger.ilog(t);
            }
        }
    }
}
