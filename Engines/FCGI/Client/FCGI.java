package Engines.FCGI.Client;

import Engines.CGI;
import Engines.CGIClient.CGIDataSender;
import Engines.FCGI.Client.Response.FCGIResponse;
import Engines.FCGI.Client.Utils.FCGIConstants;
import Server.Reqandres.Request;
import Server.Utils.Logger;
import java.io.File;

public class FCGI extends CGI {

    public FCGI(String ext, File cgiFile, Request req){
        this.extension = ext;
        this.file = cgiFile;
        this.req = req;
        this.FCGI = true;
        this.getParams();
        Logger.CGILog("FCGI environment variables created. ;;; FCGI request id : !client not initiated!", file.getName(),req.getHost());
    }

    @Override
    public void exec(String body, boolean KA){
        envs.put("CONTENT_LENGTH",String.valueOf(body.getBytes().length));
        FCGIClient client = new FCGIClient(envs,body,file.getName(),req.getHost());
        Logger.CGILog("FCGI client initiated ;;; FCGI request id : " + client.reqID, file.getName(),req.getHost());
        client.run();
        Logger.CGILog("FCGI request sent ;;; FCGI request id : " + client.reqID, file.getName(),req.getHost());
        FCGIResponse response = client.getResponse();
        Logger.CGILog("FCGI response received : " + FCGIConstants.getStatus(response.status) + ";;; FCGI request id : " + client.reqID, file.getName(),req.getHost());
        CGIDataSender ds = new CGIDataSender(req.getProt(),200);
        ds.setKeepAlive(KA);
        if (response.status == 0 && response.getErrorContent().isEmpty())
            ds.send(response.getContent(),req.out,req.getIP(),req.getID(),req.getHost());
        else{
            Logger.CGIError(response.getErrorContent() + ";;; FCGI request id : " + client.reqID,file.getName(),req.getHost());
            ds.send(response.getErrorContent(),req.out,req.getIP(),req.getID(),req.getHost());
        }
    }

}
