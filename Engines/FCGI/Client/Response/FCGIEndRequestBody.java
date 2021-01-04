package Engines.FCGI.Client.Response;

import Server.Utils.Logger;

import java.io.InputStream;

public class FCGIEndRequestBody {

    private final int appStatus;
    private final int protocolStatus;

    public FCGIEndRequestBody(int length, InputStream in){
        byte[] body = new byte[length];
        try{
            in.read(body);
        }catch (Exception ex) {
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
        this.appStatus = (body[0] << 24) + (body[1] << 16) + (body[2] << 8) + body[3];
        this.protocolStatus = body[4];
    }

    public int getAppStatus(){
        return this.appStatus;
    }

    public int getProtocolStatus(){
        return this.protocolStatus;
    }
}
