package Server.Reqandres.Responses;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Senders.Sender;

class ProtoSwitch implements Response{
    @Override
    public void init(Request req) {
        Sender snd = new Sender(req.getProt(),101);
        String up = String.valueOf(req.getHeaders().get("Upgrade"));
        snd.addHeader("Upgrade", up.trim());
        snd.setKeepAlive(false);
        snd.send(null,req);
    }
}
