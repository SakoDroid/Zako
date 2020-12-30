package Server.Method;

import Server.Reqandres.Request;
import Server.Reqandres.RequestProcessor;
import Server.Reqandres.Senders.Sender;

public class CONNECT implements Method{

    @Override
    public int run(Request rq, RequestProcessor reqp){
        Sender snd = new Sender(rq.getProt(),200);
        snd.sendConnectMethod(rq.out,rq.getIP(),rq.getID(),rq.getHost());
        return 0;
    }
}
