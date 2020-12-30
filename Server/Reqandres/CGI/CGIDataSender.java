package Server.Reqandres.CGI;

import Server.Reqandres.Senders.Sender;
import Server.Utils.Logger;
import Server.Utils.basicUtils;
import java.io.*;
import java.util.Date;

public class CGIDataSender extends Sender {

    private static InputStream in;

    private static String generateResponse(){
        return prot + " " + status + "\nDate: " + df.format(new Date()) + "\nServer: " + basicUtils.Zako + "\nConnection: closed\n" ;
    }

    public static void setInputStream(InputStream input){
        in = input;
    }

    public static void send(DataOutputStream out, String ip, int id, String host){
        Logger.glog("Sending CGI output to " + ip + "  ; id = " + id,host);
        try{
            out.writeBytes(generateResponse());
            while(in.available() > 0){
                out.write(in.read());
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