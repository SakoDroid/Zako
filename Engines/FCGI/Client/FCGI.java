package Engines.FCGI.Client;

import Engines.CGI;
import Engines.CGIClient.CGIDataSender;
import Engines.FCGI.Client.Response.FCGIResponse;
import Server.Reqandres.Request;

import java.io.File;

public class FCGI extends CGI {

    public FCGI(String ext, File cgiFile, Request req){
        this.extension = ext;
        this.file = cgiFile;
        this.req = req;
        this.getParams();
    }

    @Override
    public void exec(String body, boolean KA){
        FCGIClient client = new FCGIClient(envs,body);
        client.run();
        FCGIResponse response = client.getResponse();
        CGIDataSender ds = new CGIDataSender(req.getProt(),200);
        ds.setKeepAlive(KA);
        if (response.status != -1 && response.getErrorContent().isEmpty())
            ds.send(response.getContent(),req.out,req.getIP(),req.getID(),req.getHost());
        else
            ds.send(response.getErrorContent(),req.out,req.getIP(),req.getID(),req.getHost());
    }

}
