package Server.Reqandres.HeaderCheck;

import Server.Reqandres.Request.Request;

public class RangeRequests {

    private final Request req;

    public RangeRequests(Request req){
        this.req = req;
        String rangeHeader = req.getHeaders().get("Range");
        if (rangeHeader != null)
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
}
