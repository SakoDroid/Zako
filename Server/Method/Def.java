package Server.Method;

import Server.Reqandres.Request;
import Server.Reqandres.RequestProcessor;

public class Def implements Method{

    @Override
    public int run(Request req, RequestProcessor reqp){
        return 1;
    }
}
