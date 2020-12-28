package Server.Method;

import Server.Reqandres.Request;
import Server.Reqandres.RequestProcessor;
import Server.Reqandres.Senders.FileSender;

public class CONNECT implements Method{

    @Override
    public int run(Request rq, RequestProcessor reqp){
        FileSender.setProt(rq.getProt());
        FileSender.sendConnectMethod(rq.out,rq.getIP(),rq.getID(),rq.getHost());
        return 0;
    }
}
