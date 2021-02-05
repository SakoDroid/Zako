package Server.Reqandres;

import Server.Reqandres.Request.Request;
import Server.Utils.Logger;
import java.net.Socket;
import java.io.*;

public class Proxy {

    private final Socket client;

    public Proxy(String[] address, String read, Request req){
        this.client = req.getSock();
        try{
            Socket s = new Socket(address[0], Integer.parseInt(address[1]));
            new Piper(read, req.is, s.getOutputStream(), s);
            new Piper(s.getInputStream(), req.out, s);
            Logger.glog("Forwarding request for " + req.getHost() + " from " + req.getIP() + " to " + address[0] + ":" + address[1],req.getHost());
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    private class Piper extends Thread {

        private String readReq;
        private final OutputStream out;
        private final InputStream in;
        private final Socket connection;

        public Piper (String str,InputStream is, OutputStream os, Socket s){
            this.readReq = str;
            this.out = os;
            this.connection = s;
            this.in = is;
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
                if (readReq != null)
                    out.write(readReq.getBytes());
                do{
                    System.out.println("for");
                    in.transferTo(out);
                    System.out.println("done");
                }
                while (!client.isClosed());
            }catch(Exception ex){
                System.out.println("ex");
                try {
                    connection.close();
                }catch (Exception ignored){}
                Logger.logException(ex);
            }
        }
    }
}
