package Server.Reqandres;

import java.io.*;
import Server.Utils.*;
import Server.Reqandres.Senders.*;

public class Response {

    private final RequestProcessor reqes;
    private final Request request;


    public Response(RequestProcessor rq, Request req){
        this.reqes = rq;
        this.request = req;
        this.handleRes();
    }

    private void handleRes(){
        try{
            Logger.glog("Preparing response to " + request.getIP() + "  ; id = " + request.getID(), request.getHost());
            Server.API.Factory.getAPI(request.Path).init(request, reqes);
        }catch (Exception ex) {
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }
    
    private void sendCode(int code){
        FileSender.setProt(request.getProt());
        FileSender.setContentType("text/html");
        FileSender.setStatus(code);
        FileSender.sendFile(request.getMethod(),new File(Configs.getCWD() + "/default_pages/" + code + ".html"),request.out,request.getIP(),request.getID(),request.getHost());
    }
}