package Server.Reqandres.Responses;

import Server.Reqandres.Request.Request;

class Authenticate implements Response{
    @Override
    public void init(Request req) {
        Server.HttpAuth.Interface.getInstance().send401(req);
    }
}
