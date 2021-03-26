package Server.Utils.Reader;

import Server.Reqandres.Request.Request;
import Server.Utils.Logger;
import java.io.InputStream;

public class RequestReader {

    private boolean hasBody;
    private final Request req;

    public RequestReader(Request req){
        this.req = req;
    }

    public void readHeaders(){

    }

    public void readBody(){

    }

    public boolean hasBody(){
        return this.hasBody;
    }

    private String readLine(InputStream in){
        StringBuilder sb = new StringBuilder();
        int i;
        if (req.getSocket().isClosed())
            return null;
        try{
            i = in.read();
            if (i == -1) return null;
            while (i != 13){
                if (i != 10) sb.append((char)i);
                i = in.read();
                if (i == -1) break;
            }
        }catch(Exception ex){
            try {
                req.getSocket().close();
            }catch (Exception ex2){
                Logger.logException(ex2);
            }
            Logger.logException(ex);
            return null;
        }
        return sb.toString();
    }
}
