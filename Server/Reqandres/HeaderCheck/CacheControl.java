package Server.Reqandres.HeaderCheck;

import Server.Utils.HashComputer;
import java.io.File;
import java.util.HashMap;

public class CacheControl {

    private int status = 200;

    public void decide(HashMap<String,String> headers, File fl){
        if (headers.containsKey("If-None-Match")){
            if (headers.get("If-None-Match").replace("\"","").trim().equals(
                    new HashComputer(fl).computeHash()
            ))
                this.status = 304;
        }
        else if (headers.containsKey("If-Match")){
            if (headers.get("If-Match").replace("\"","").trim().equals(
                    new HashComputer(fl).computeHash()
            ))
                this.status = 412;
        }
    }

    public int getStatus(){
        return this.status;
    }
}
