package Server.Method;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Request.RequestProcessor;

public class Def implements Method{

    @Override
    public int run(Request req, RequestProcessor reqp){
        return 1;
    }
}
