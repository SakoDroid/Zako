package Engines.FastCGI;

import Server.Reqandres.Senders.Sender;
import Server.Utils.Logger;
import Server.Utils.basicUtils;
import java.io.*;
import java.util.Date;

public class CGIDataSender extends Sender {

    private InputStream in;

    public CGIDataSender(String prot,int status,InputStream is){
        super(prot, status);
        this.in = is;
    }

    private String generateResponse(){
        String out = prot + " " + status + "\nDate: " + df.format(new Date()) + "\nServer: " + basicUtils.Zako;
        if (keepAlive) out += "\nConnection: keep-alive";
        else out += "\nConnection: close";
        return out;
    }

    public void send(DataOutputStream out, String ip, int id, String host){
        Logger.glog("Sending CGI output to " + ip + "  ; id = " + id,host);
        try{
            out.writeBytes(generateResponse());
            int i;
            while((i = in.read()) != -1){
                out.write(i);
            }
            out.flush();
            out.close();
            basicUtils.delID(id);
            Logger.glog(ip + "'s request handled successfully!" + "  ; id = " + id,host);
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }
}