package Server.Reqandres.Responses;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Senders.QuickSender;

class ErrorSender implements Response{
    @Override
    public void init(Request req) {
        if (req.getResponseCode() != 400)
            new QuickSender(req).sendCode(req.getResponseCode());
        else
            new QuickSender(req).sendBadReq(req.getErrorReason());
    }
}
