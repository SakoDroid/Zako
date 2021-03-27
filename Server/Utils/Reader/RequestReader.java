package Server.Utils.Reader;

import Server.Reqandres.Request.Request;
import Server.Utils.Logger;
import java.io.FileOutputStream;
import java.io.InputStream;

public class RequestReader {

    private boolean hasBody;
    private int bodyLength;
    private final Request req;
    private String firstLine;
    private FileOutputStream fw;

    public RequestReader(Request req){
        this.req = req;
        try {
            fw = new FileOutputStream(req.getCacheFile());
        }catch (Exception ex){
            Logger.logException(ex);
        }
    }

    public void readHeaders(){
        try{
            firstLine = this.readLine(req.is);
            if (firstLine != null && firstLine.length() > 5){
                fw.write(firstLine.getBytes());
                fw.write( "\r\n".getBytes());
                String line;
                while ((line = this.readLine(req.is)) != null){
                    if (line.isEmpty())
                        break;
                    fw.write(line.getBytes());
                    fw.write("\r\n".getBytes());
                    String[] temp = line.split(":",2);
                    if (temp.length != 2)
                        break;
                    req.addHeader(temp[0].trim(),temp[1].trim());
                }
                this.hasBody = req.getHeaders().containsKey("Content-Type");
                if (this.hasBody){
                    String cntLen = req.getHeaders().get("Content-Length");
                    if (cntLen != null)
                        this.bodyLength = Integer.parseInt(cntLen);
                    else
                        this.bodyLength = -1;
                }
            }
        }catch (Exception ex){
            Logger.logException(ex);
        }
    }

    public void readBody(){
        try {
            fw.write(req.is.readNBytes(bodyLength + 1));
        } catch (Exception ex) {
            Logger.logException(ex);
        }
    }

    public void finishReading(){
        try {
            fw.flush();
            fw.close();
        }catch (Exception ex){
            Logger.logException(ex);
        }
    }

    public boolean hasBody(){
        return this.hasBody;
    }

    public int getBodyLength(){
        return this.bodyLength;
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
