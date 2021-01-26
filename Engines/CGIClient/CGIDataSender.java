package Engines.CGIClient;

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

    public void send(DataOutputStream out, String ip, int id, String host){
        Logger.glog("Sending CGI output to " + ip + "  ; id = " + id,host);
        try{
            out.writeBytes(generateResponse());
            int i = in.read();
            if (i != 10) out.writeBytes("\n");
            out.write(i);
            while((i = in.read()) != -1){
                out.write(i);
            }
            out.flush();
            basicUtils.delID(id);
            Logger.glog(ip + "'s request handled successfully!" + "  ; id = " + id,host);
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    public void sendFCGIData(String data, DataOutputStream out, String ip, int id, String host){
        Pattern ptn = Pattern.compile("Status: .*");
        Matcher mc = ptn.matcher(data);
        if(mc.find()) status = mc.group().replace("Status: ","");
        data = data.replace("Status: " + status,"");
        try{
            out.writeBytes(generateResponse());
            if (data.startsWith("\n")) out.writeBytes(data);
            else out.writeBytes("\n" + data);
            out.flush();
            basicUtils.delID(id);
            Logger.glog(ip + "'s request handled successfully!" + "  ; id = " + id,host);
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }
    public void sendFCGIData(byte[] data, DataOutputStream out, String ip, int id, String host){
        String res = new String(data);
        Pattern ptn = Pattern.compile("Status: .*");
        Matcher mc = ptn.matcher(res);
        if(mc.find()) status = mc.group().replace("Status: ","");
        try{
            out.writeBytes(generateResponse());
            if (data[0] != 10) out.writeBytes("\n");
            out.write(data);
            out.flush();
            basicUtils.delID(id);
            Logger.glog(ip + "'s request handled successfully!" + "  ; id = " + id,host);
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

}