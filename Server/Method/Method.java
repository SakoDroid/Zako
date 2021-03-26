package Server.Method;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Request.RequestProcessor;

public interface Method {

    int run(Request req, RequestProcessor reqp);

}
