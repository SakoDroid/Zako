package Server.Reqandres.Senders;

import Server.Reqandres.Request.Request;
import Server.Utils.Enums.Methods;
import Server.Utils.Logger;
import java.util.HashMap;
import Server.Utils.basicUtils;

public class Sender {

    protected String prot;
    protected String status;
    protected String contentType;
    protected String cookie;
    protected String ext;
    protected boolean keepAlive = false;
    protected final HashMap<String,String> headers = new HashMap<>();

    public Sender (String prot, int status){
        this.prot = prot;
        this.setStatus(status);
    }

    protected String generateHeaders(Request req){
        new HeaderGenerator(null,null,req).generate(this.headers);
        return this.turnHeadersIntoString(req.getProt());
    }

    protected String generateHeaders(Request req,int bodyLength){
        new HeaderGenerator(null,null,req).generate(this.headers,bodyLength);
        return this.turnHeadersIntoString(req.getProt());
    }

    protected String turnHeadersIntoString(String proto){
        StringBuilder sb = new StringBuilder(proto + " " + this.status + "\r\n");
        for (String key : headers.keySet())
            sb.append(key).append(": ").append(headers.get(key)).append("\r\n");
        if (cookie != null)
            sb.append("Set-Cookie: ").append(cookie).append("\r\n");
        return sb.append("Status: ").append(this.status).append("\r\n\r\n").toString();
    }

    public void setKeepAlive(boolean ka){
        this.keepAlive = ka;
    }

    public void setStatus(int statusCode){
        this.status = basicUtils.getStatusCodeComp(statusCode);
    }

    public void setExtension(String ext){
        this.ext = ext;
    }

    public void addHeader(String value, String key){
        this.headers.put(value.trim(),key.trim());
    }

    public void addCookie(String ck){
        if(cookie == null) cookie = ck;
        else cookie += ";" + ck;
    }

    public void sendConnectMethod(Request req){
        try{
            if (!req.getSocket().isClosed()){
                Logger.glog("Sending back connect method response to " + req.getIP() + "  ; debug_id = " + req.getID(), req.getHost());
                req.getOutStream().writeBytes(this.generateHeaders(req));
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
                req.getOutStream().writeBytes(data == null ? generateHeaders(req) : generateHeaders(req,data.getBytes().length));
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