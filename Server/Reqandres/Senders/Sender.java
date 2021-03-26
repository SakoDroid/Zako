package Server.Reqandres.Senders;

import Server.Reqandres.Request.Request;
import Server.Utils.Logger;
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


    public void sendOptionsMethod(Request req){
        try{
            Logger.glog("Sending back options method response to " + req.getIP() + "  ; debug_id = " + req.getID(),req.getHost());
            req.getOutStream().writeBytes(prot + " 200 OK\nDate: " + df.format(new Date()) + "\nServer: " + basicUtils.Zako + "\nConnection: close\nAllow: GET,HEAD,POST,OPTIONS,TRACE,CONNECT,PUT,DELETE");
            req.getOutStream().flush();
            req.getOutStream().close();
            Logger.glog(req.getIP() + "'s request handled successfully!" + "  ; debug_id = " + req.getID(),req.getHost());
            req.clearRequest();
        }catch (Exception ex){
            Logger.logException(ex);
        }
    }

    public void sendConnectMethod(Request req){
        try{
            Logger.glog("Sending back connect method response to " + req.getIP() + "  ; debug_id = " + req.getID(),req.getHost());
            req.getOutStream().writeBytes(prot + " 200 Connection established\nDate: " + df.format(new Date()) + "\nServer: " + basicUtils.Zako);
            req.getOutStream().flush();
            req.getOutStream().close();
            Logger.glog(req.getIP() + "'s request handled successfully!" + "  ; debug_id = " + req.getID(),req.getHost());
            req.clearRequest();
        }catch (Exception ex){
            Logger.logException(ex);
        }
    }

    public void send(String data, Request req){
        Logger.glog("Sending response to " + req.getID() + "  ; debug_id = " + req.getID(),req.getHost());
        try{
            req.getOutStream().writeBytes(generateResponse(data));
            if (!this.keepAlive) {
                req.getOutStream().flush();
                req.getOutStream().close();
            }
            Logger.glog(req.getID() + "'s request handled successfully!" + "  ; debug_id = " + req.getID(),req.getHost());
            req.clearRequest();
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }
}