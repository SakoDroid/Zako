package Engines.FCGI.Client.Request;

import Engines.FCGI.Client.Utils.Configs;
import Server.Utils.Logger;

import java.io.OutputStream;

public class FCGIRequest {

    private final FCGIRequestHeader header;
    private final FCGIRequestComponent body;

    public FCGIRequest(FCGIRequestHeader header, FCGIRequestComponent body){
        if (header != null && body != null){
            this.header = header;
            this.body = body;
        }else throw new NullPointerException("Header or body can not be null");
    }

    public void send(OutputStream os){
        try{
            header.send(os);
            body.send(os);
            byte[] paddingData = Configs.padding;
            if (paddingData != null && paddingData.length > 0) os.write(paddingData);
        }catch (Exception ex) {
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

}
