package Server.Reqandres.HeaderCheck;

import Server.Reqandres.Request.Request;
import Server.Utils.Configs.Configs;
import Server.Utils.HeaderRelatedTools.*;
import java.io.File;

public class RangeRequests {

    private final Request req;
    private boolean isIfRangeValid = true;

    public RangeRequests(Request req){
        this.req = req;
        String ifRangeHeader = req.getHeaders().get("If-Range");
        if (ifRangeHeader != null)
            this.processIfRange(ifRangeHeader);
        String rangeHeader = req.getHeaders().get("Range");
        if (rangeHeader != null && isIfRangeValid)
            this.processRange(rangeHeader);
    }

    private void processRange(String header){
        if (header.startsWith("bytes")){
            req.setBoundary();
            header = header.replace("bytes=","");
            for (String range : header.split(",")){
                if (range.startsWith("-"))
                    req.addRange(new long[]{-1,Long.parseLong(range.replace("-","").trim())});
                else{
                    String[] vals = range.split("-",2);
                    req.addRange(new long[]{Long.parseLong(vals[0]),Long.parseLong(vals[1])});
                }
            }
        }
    }

    private void processIfRange(String header){
        header = header.replace("w/","").replace("\"","");
        String hash = new HashComputer(new File(Configs.getMainDir(req.getHost()) + req.getPath())).computeHash();
        if (header.equals(hash))
            isIfRangeValid = true;
        else{
            String lm = new LMGenerator(new File(Configs.getMainDir(req.getHost()) + req.getPath())).generate();
            isIfRangeValid = header.equals(lm);
        }
    }
}
