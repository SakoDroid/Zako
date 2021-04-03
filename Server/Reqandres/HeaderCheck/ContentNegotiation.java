package Server.Reqandres.HeaderCheck;

import Server.Reqandres.Request.Request;
import Server.Utils.Compression.Algorithm;
import java.util.HashMap;

class ContentNegotiation {

    private final Request req;

    public ContentNegotiation(Request req){
        this.req = req;
        this.process();
    }

    private void process(){
        String accept = req.getHeaders().get("Accept");
        if (accept != null)
            this.processAccept(accept);
        else
            req.getAccepts().add("*/*");
        String acceptEncoding = req.getHeaders().get("Accept-Encoding");
        if (acceptEncoding != null)
            this.processAcceptEncoding(acceptEncoding);
    }

    private void processAccept(String header){
        for (String MIME : header.split(","))
            req.getAccepts().add(MIME.split(";",2)[0].trim());
    }

    private void processAcceptEncoding(String header){
        HashMap<String,Double> encodings = new HashMap<>();
        double highest = 0.0;
        String high = "";
        for (String encoding : header.split(",")){
            String[] temp = encoding.split(";");
            if (temp.length > 1){
                encodings.put(temp[0].trim(),Double.parseDouble(temp[1].replace("q=","")));
            }else
                encodings.put(encoding.trim(), 1.0);
        }
        if (encodings.containsKey("deflate")){
            highest = 2.0;
            high = "deflate";
        }
        for (String db : encodings.keySet()){
            if (db.equals("compress") || db.equals("br"))
                continue;
            double q = encodings.get(db);
            if (q >= highest){
                high = db;
                highest = q;
            }
        }
        switch (high){
            case "gzip","*" -> {
                req.setCompression(true);
                req.setCompressionAlg(Algorithm.gzip);
            }
            case "deflate" -> {
                req.setCompression(true);
                req.setCompressionAlg(Algorithm.deflate);
            }
        }
    }
}
