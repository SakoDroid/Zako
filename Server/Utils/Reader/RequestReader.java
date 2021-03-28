package Server.Utils.Reader;

import Server.Reqandres.Request.Request;
import Server.Utils.Logger;
import Server.Utils.Enums.Methods;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.regex.*;

public class RequestReader {

    private boolean hasBody = false;
    private boolean protoFound = false;
    private boolean pathFound = false;
    private boolean hostFound = false;
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
                this.processFirstLine();
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
                this.hostFound = req.getHeaders().containsKey("Host");
                if (hostFound)
                    req.setHost(req.getHeaders().get("Host"));
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

    private void processFirstLine(){
        Pattern pathPattern = Pattern.compile(" /[^ ]*");
        Matcher pathMatcher = pathPattern.matcher(firstLine);
        if (pathMatcher.find()) {
            pathFound = true;
            Pattern protoPattern = Pattern.compile("HTTP/\\d[.]?\\d?");
            Matcher protoMatcher = protoPattern.matcher(firstLine);
            if (protoMatcher.find()){
                protoFound = true;
                req.setOrgPath(pathMatcher.group());
                req.setPath(req.getOrgPath());
                Matcher mch = Pattern.compile("/[^?]+").matcher(req.getOrgPath());
                if (mch.find())
                    req.setPath(mch.group());
                req.setProt(protoMatcher.group());
                req.setMethod(switch (this.firstLine.split(" ", 3)[0]) {
                    case "GET" -> Methods.GET;
                    case "POST" -> Methods.POST;
                    case "PUT" -> Methods.PUT;
                    case "HEAD" -> Methods.HEAD;
                    case "DELETE" -> Methods.DELETE;
                    case "CONNECT" -> Methods.CONNECT;
                    case "OPTIONS" -> Methods.OPTIONS;
                    case "TRACE" -> Methods.TRACE;
                    default -> Methods.UNKNOWN;
                });
            }
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

    public boolean isProtoFound(){
        return this.protoFound;
    }

    public boolean isPathFound(){
        return this.pathFound;
    }

    public boolean isHostFound() {
        return this.hostFound;
    }

    public int getBodyLength(){
        return this.bodyLength;
    }

    public String getFirstLine() {
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
