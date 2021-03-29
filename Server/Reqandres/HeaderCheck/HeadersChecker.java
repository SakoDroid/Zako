package Server.Reqandres.HeaderCheck;

import Server.Reqandres.Request.Request;
import Server.Utils.Configs.Configs;
import java.io.File;

public class HeadersChecker {

    private final Request req;
    private int status = 200;

    public HeadersChecker(Request req){
        this.req = req;
        this.startChecking();
    }

    private void startChecking(){
        Conditionals cc = new Conditionals();
        cc.decide(req.getHeaders(),new File(Configs.getMainDir(req.getHost()) + req.getPath()),req.getMethod());
        this.status = cc.getStatus();
        if (this.status == 200){

        }
    }

    public int getStatus(){
        return this.status;
    }
}
