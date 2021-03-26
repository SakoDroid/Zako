package Server.Method;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Request.RequestProcessor;
import Server.Reqandres.Senders.Sender;

public class CONNECT implements Method{

    @Override
    public int run(Request rq, RequestProcessor reqp){
        Sender snd = new Sender(rq.getProt(),200);
        snd.sendConnectMethod(rq);
        return 0;
    }
}
