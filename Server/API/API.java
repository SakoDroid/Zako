package Server.API;

import Server.Reqandres.Request.ServerRequest;
import Server.Reqandres.Request.RequestProcessor;

public interface API {

    void init(ServerRequest req, RequestProcessor reqp);
}
