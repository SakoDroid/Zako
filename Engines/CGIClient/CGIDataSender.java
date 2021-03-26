package Engines.CGIClient;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Senders.Sender;
import Server.Utils.Logger;
import Server.Utils.basicUtils;
import java.io.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CGIDataSender extends Sender {

    private InputStream in;

    public CGIDataSender(String prot,int status,InputStream is){
        super(prot, status);
        this.in = is;
    }

    public CGIDataSender(String prot,int status){
        super(prot, status);
    }

    private String generateResponse(){
        String out = prot + " " + status + "\nDate: " + df.format(new Date()) + "\nServer: " + basicUtils.Zako;
        if (Double.parseDouble(prot.replace("HTTP/","")) < 2){
            if (keepAlive) out += "\nConnection: keep-alive";
            else out += "\nConnection: close";
        }
        return out;
    }

    public void send(Request req){
        Logger.glog("Sending CGI output to " + req.getIP() + "  ; debug_id = " + req.getID(),req.getHost());
        try{
            req.out.writeBytes(generateResponse());
            int i = in.read();
            if (i != 10) req.out.writeBytes("\n");
            req.out.write(i);
            while((i = in.read()) != -1){
                req.out.write(i);
            }
            Logger.glog(req.getIP() + "'s request handled successfully!" + "  ; debug_id = " + req.getID(),req.getHost());
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    public void sendFCGIData(String data, Request req){
        Pattern ptn = Pattern.compile("Status: .*");
        Matcher mc = ptn.matcher(data);
        if(mc.find()) status = mc.group().replace("Status: ","");
        data = data.replace("Status: " + status,"");
        try{
            req.out.writeBytes(generateResponse());
            if (data.startsWith("\n")) req.out.writeBytes(data);
            else req.out.writeBytes("\n" + data);
            Logger.glog(req.getIP() + "'s request handled successfully!" + "  ; debug_id = " + req.getID(),req.getHost());
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }
    public void sendFCGIData(byte[] data, Request req){
        String res = new String(data);
        Pattern ptn = Pattern.compile("Status: .*");
        Matcher mc = ptn.matcher(res);
        if(mc.find()) status = mc.group().replace("Status: ","");
        try{
            req.out.writeBytes(generateResponse());
            if (data[0] != 10) req.out.writeBytes("\n");
            req.out.write(data);
            Logger.glog(req.getIP() + "'s request handled successfully!" + "  ; debug_id = " + req.getID(),req.getHost());
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

}