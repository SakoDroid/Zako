package Server.Reqandres.Senders;

import Server.Utils.Configs;
import Server.Utils.Logger;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.text.SimpleDateFormat;
import Server.Utils.basicUtils;

public class Sender {

    protected SimpleDateFormat df = new SimpleDateFormat("E, dd MM yyyy HH:mm:ss z");
    protected String prot;
    protected String status;
    protected String contentType;
    protected String cookie;
    protected String customHeaders = "";
    protected boolean keepAlive = false;

    public Sender (String prot, int status){
        this.prot = prot;
        this.setStatus(status);
    }

    private String generateResponse(String body){
        String out = prot + " " + status + "\nDate: " + df.format(new Date()) + "\nServer: " + basicUtils.Zako + "\nStatus: " + this.status;
        if (body != null)
            out += "\nContent-Length: " + body.length();
        if (contentType != null)
            out += "\nContent-Type: " + contentType;
        if (cookie != null)
            out += "\nSet-Cookie: " + cookie;
        if (Double.parseDouble(prot.replace("HTTP/","")) < 2){
            if (keepAlive) out += "\nConnection: keep-alive";
            else out += "\nConnection: close";
        }
        if (!customHeaders.isEmpty())
            out += "\n" + customHeaders;
        if (body != null)
            out += "\n\n" + body;
        else out += "\n\n";
        return out;
    }

    public void setKeepAlive(boolean ka){
        this.keepAlive = ka;
    }

    public void setStatus(int statusCode){
        this.status = basicUtils.getStatusCodeComp(statusCode);
    }

    public void setContentType(String cnt){
        contentType = cnt;
    }

    public void addHeader(String header){
        if (customHeaders.isEmpty())
            customHeaders += header;
        else {
            if (customHeaders.endsWith("\n"))
                customHeaders += header;
            else customHeaders += "\n" + header;
        }
    }

    public void addCookie(String ck){
        if(cookie == null) cookie = ck;
        else cookie += ";" + ck;
    }


    public void sendOptionsMethod(DataOutputStream out,String ip,int id,String host){
        try{
            Logger.glog("Sending back options method response to " + ip + "  ; id = " + id,host);
            out.writeBytes(prot + " 200 OK\nDate: " + df.format(new Date()) + "\nServer: " + basicUtils.Zako + "\nConnection: close\nAllow: GET,HEAD,POST,OPTIONS,TRACE,CONNECT,PUT,DELETE\nx-IPS-Allowed-For-PUT-DELETE: ");
            File fl = null;
            if (System.getProperty("os.name")
                    .toLowerCase().contains("windows"))
                fl = new File(System.getProperty("user.dir") + "/Configs/sec/ILPD");
            else if (System.getProperty("os.name")
                    .toLowerCase().contains("linux"))
                fl = new File("/etc/zako-web/sec/ILPD");
            assert fl != null;
            BufferedReader bf = new BufferedReader(new FileReader(fl));
            String line;
            String ips = "";
            while ((line = bf.readLine()) != null){
                if (!line.startsWith("#")) ips += line + ", ";
            }
            bf.close();
            if (ips.isEmpty()) out.writeBytes("");
            else out.writeBytes(ips.substring(0,ips.length()-1));
            out.flush();
            out.close();
            basicUtils.delID(id);
            Logger.glog(ip + "'s request handled successfully!" + "  ; id = " + id,host);
        }catch (Exception ex){
            Logger.logException(ex);
        }
    }

    public void sendConnectMethod(DataOutputStream out,String ip,int id,String host){
        try{
            Logger.glog("Sending back connect method response to " + ip + "  ; id = " + id,host);
            out.writeBytes(prot + " 200 Connection established\nDate: " + df.format(new Date()) + "\nServer: " + basicUtils.Zako);
            out.flush();
            out.close();
            basicUtils.delID(id);
            Logger.glog(ip + "'s request handled successfully!" + "  ; id = " + id,host);
        }catch (Exception ex){
            Logger.logException(ex);
        }
    }

    public void send(String data, DataOutputStream out,String ip,int id,String host){
        Logger.glog("Sending response to " + ip + "  ; id = " + id,host);
        try{
            out.writeBytes(generateResponse(data));
            if (!this.keepAlive) {
                out.flush();
                out.close();
            }
            basicUtils.delID(id);
            Logger.glog(ip + "'s request handled successfully!" + "  ; id = " + id,host);
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }
}