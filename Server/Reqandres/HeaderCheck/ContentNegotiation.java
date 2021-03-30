package Server.Reqandres.HeaderCheck;

import Server.Reqandres.Request.Request;
import Server.Utils.Compression.Algorithm;
import java.util.TreeMap;

class ContentNegotiation {

    private final Request req;

    public ContentNegotiation(Request req){
        this.req = req;
        this.process();
    }

    private void process(){
        String acceptEncoding = req.getHeaders().get("Accept-Encoding");
        if (acceptEncoding != null)
            this.processAcceptEncoding(acceptEncoding);
    }

    private void processAcceptEncoding(String header){
        TreeMap<Double,String> encodings = new TreeMap<>();
        for (String encoding : header.split(",")){
            String[] temp = encoding.split(";");
            if (temp.length > 1){
                encodings.put(Double.parseDouble(temp[1].replace("q=","")),temp[0].trim());
            }else
                encodings.put(0.0,encoding.trim());
        }
        for (double db : encodings.keySet()){
            switch (encodings.get(db)){
                case "gzip","*" -> {
                    req.setCompression(true);
                    req.setCompressionAlg(Algorithm.gzip);
                }
            }
            break;
        }
    }
}
