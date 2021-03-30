package Server.Method;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Senders.Sender;

public class CONNECT implements Method{

    @Override
    public void run(Request rq){
        Sender snd = new Sender(rq.getProt(),200);
        snd.sendConnectMethod(rq);
    }
}
