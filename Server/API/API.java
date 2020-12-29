package Server.API;

import Server.Reqandres.Request;
import Server.Reqandres.RequestProcessor;

public interface API {

    void init(Request req, RequestProcessor reqp);
}
