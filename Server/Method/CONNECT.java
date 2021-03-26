package Server.Method;

import Server.Reqandres.Request.ServerRequest;
import Server.Reqandres.Request.RequestProcessor;
import Server.Reqandres.Senders.Sender;

public class CONNECT implements Method{

    @Override
    public int run(ServerRequest rq, RequestProcessor reqp){
        Sender snd = new Sender(rq.getProt(),200);
        snd.sendConnectMethod(rq);
        return 0;
    }
}
