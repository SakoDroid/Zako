package Server.Reqandres.Senders;

import Server.Reqandres.Request.Request;
import Server.Utils.Compression.CompressorFactory;
import Server.Utils.Configs.HTAccess;
import Server.Utils.Enums.Methods;
import Server.Utils.Logger;
import java.io.FileInputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Objects;
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

    protected String getConnectionHeader(Request req){
        String out = "";
        if (keepAlive){
            out += "\nConnection: keep-alive\nKeep-Alive: ";
            String kah = req.getHeaders().get("Keep-Alive");
            out += Objects.requireNonNullElseGet(kah, () -> "timeout=" + HTAccess.getInstance().getKeepAliveTimeout(req.getHost()) + ", max=" + HTAccess.getInstance().getMNORPC(req.getHost()));
        }
        else out += "\nConnection: close";
        return out;
    }

    private String generateResponse(String body,Request req){
        String out = prot + " " + status + "\r\nDate: " + df.format(new Date()) + "\r\nServer: " + basicUtils.Zako + "\r\nStatus: " + this.status;
        if (req.getMethod() != Methods.HEAD &&  body != null)
            out += "\r\nContent-Length: " + body.length() + "\r\nContent-Type: " + contentType;
        if (cookie != null)
            out += "\r\nSet-Cookie: " + cookie;
        if (Double.parseDouble(prot.replace("HTTP/","")) < 2)
            out += this.getConnectionHeader(req);
        if (req.getHeaders().containsKey("Origin"))
            out += "Access-Control-Allow-Credentials: " + HTAccess.getInstance().isCredentialsAllowed(req.getHost());
        if (!customHeaders.isEmpty())
            out += "\r\n" + customHeaders;
        return out + "\r\n\r\n";
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
            else customHeaders += "\r\n" + header;
        }
    }

    public void addCookie(String ck){
        if(cookie == null) cookie = ck;
        else cookie += ";" + ck;
    }

    public void sendConnectMethod(Request req){
        try{
            if (!req.getSocket().isClosed()){
                Logger.glog("Sending back connect method response to " + req.getIP() + "  ; debug_id = " + req.getID(), req.getHost());
                req.getOutStream().writeBytes(prot + " 200 Connection established\r\nDate: " + df.format(new Date()) + "\r\nServer: " + basicUtils.Zako);
                req.getOutStream().flush();
                req.getOutStream().close();
                Logger.glog(req.getIP() + "'s request handled successfully!" + "  ; debug_id = " + req.getID(), req.getHost());
            }else
                Logger.glog("Connection closed with " + req.getIP() + " without sending response.; debug_id = " + req.getID(),req.getHost());
        }catch (Exception ex){
            Logger.logException(ex);
        }
    }

    public void send(String data, Request req){
        try{
            if (!req.getSocket().isClosed()){
                Logger.glog("Sending response to " + req.getID() + "  ; debug_id = " + req.getID(),req.getHost());
                req.getOutStream().writeBytes(generateResponse(data, req));
                if (req.getMethod() != Methods.HEAD && data != null)
                        req.getOutStream().writeBytes(data);
                if (!this.keepAlive) {
                    req.getOutStream().flush();
                    req.getOutStream().close();
                }
                Logger.glog(req.getIP() + "'s request handled successfully!  ; debug_id = " + req.getID(), req.getHost());
            }else
                Logger.glog("Connection closed with " + req.getIP() + " without sending response.; debug_id = " + req.getID(),req.getHost());
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }
}