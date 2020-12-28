package Server.Method;

import Server.Reqandres.Request;
import Server.Reqandres.RequestProcessor;

public interface Method {

    int run(Request req, RequestProcessor reqp);

}
