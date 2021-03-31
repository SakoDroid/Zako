package Server.Reqandres.HeaderCheck;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Senders.QuickSender;
import Server.Utils.Configs.Configs;

public class Controls {

    private final Request req;

    public Controls(Request req){
        this.req = req;
        this.process();
    }

    private void process(){
        String expect = req.getHeaders().get("Expect");
        if (expect != null)
            this.processExpect(expect);
    }

    private void processExpect(String header){
        if (header.equals("100-continue")){
            String contentLength = req.getHeaders().get("Content-Length");
            if (contentLength != null){
                if (Long.parseLong(contentLength) <= Configs.getPostBodySize(req.getHost()))
                    new QuickSender(req).sendCode(100);
                else
                    req.setResponseCode(417);
            }else
                req.setResponseCode(400);
        }
    }
}
