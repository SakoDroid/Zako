package Server.Method;

import Server.Reqandres.Request.ServerRequest;
import Server.Reqandres.Request.RequestProcessor;

public interface Method {

    int run(ServerRequest req, RequestProcessor reqp);

}
