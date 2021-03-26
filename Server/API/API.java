package Server.API;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Request.RequestProcessor;

public interface API {

    void init(Request req, RequestProcessor reqp);
}
