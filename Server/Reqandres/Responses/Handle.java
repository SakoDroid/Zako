package Server.Reqandres.Responses;

import Server.Method.Factory;
import Server.Reqandres.Request.Request;

class Handle implements Response{

    @Override
    public void init(Request req) {
       new Factory().getMt(req.getMethod()).run(req);
    }
}
