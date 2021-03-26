package Server.Method;

import Server.Reqandres.Request.ServerRequest;
import Server.Reqandres.Request.RequestProcessor;

public class Def implements Method{

    @Override
    public int run(ServerRequest req, RequestProcessor reqp){
        return 1;
    }
}
