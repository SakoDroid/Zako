package Server.Utils.Reader;

import Server.Reqandres.Request.Request;
import Server.Utils.Logger;

import java.io.FileWriter;
import java.io.InputStream;

public class RequestReader {

    private boolean hasBody;
    private final Request req;
    private String firstLine;
    private FileWriter fw;

    public RequestReader(Request req){
        this.req = req;
        try {
            fw = new FileWriter(req.getCacheFile());
        }catch (Exception ex){
            Logger.logException(ex);
        }
    }

    public void readHeaders(){
        try{
            firstLine = this.readLine(req.is);
            if (firstLine != null){
                fw.write(firstLine);
                fw.write( "\r\n");
                String line;
                while ((line = this.readLine(req.is)) != null){
                    if (line.isEmpty())
                        break;
                    fw.write(line);
                    fw.write(line);
                    String[] temp = line.split(":",2);
                    if (temp.length != 2)
                        break;
                    req.addHeader(temp[0].trim(),temp[1].trim());
                }
            }
        }catch (Exception ex){
            Logger.logException(ex);
        }
    }

    public void readBody(){

    }

    public boolean hasBody(){
        return this.hasBody;
    }

    public String getFirstLine(){
        return this.firstLine;
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
