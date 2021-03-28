package Server.Utils;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Senders.QuickSender;
import java.net.Socket;
import java.io.*;

public class Proxy {

    private final Request req;

    public Proxy(String[] address, Request req){
        this.req = req;
        String readReq;
        try{
            FileInputStream fis = new FileInputStream(req.getCacheFile());
            readReq = new String(fis.readAllBytes());
            Socket s = new Socket(address[0], Integer.parseInt(address[1]));
            new Piper(readReq, req.getInStream(), s.getOutputStream(), s);
            new Piper(s.getInputStream(), req.getOutStream(), s);
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
                if (readReq != null){
                    out.write(readReq.getBytes());
                    out.write(("Forwarded: by=" + basicUtils.LocalHostIP + ";for=" + req.getIP() + ";host=" + req.getHost() + ";proto=" + req.getProt() + "\r\n").getBytes());
                }
                in.transferTo(out);
            }catch(Exception ex){
                Logger.logException(ex);
                if (ex.toString().contains("Timeout")) new QuickSender(req).sendCode(504);
                else new QuickSender(req).sendCode(502);
                try {
                    connection.close();
                }catch (Exception ignored){}
                Logger.logException(ex);
            }
        }
    }
}
