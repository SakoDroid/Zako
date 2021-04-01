package Server.Reqandres.Senders;

import Server.Reqandres.Request.Request;
import Server.Utils.Configs.FileTypes;
import Server.Utils.Configs.HTAccess;
import Server.Utils.Enums.Methods;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class HeaderGenerator {

    private final String ext;
    private final String filePath;
    private final Request req;
    private final SimpleDateFormat df = new SimpleDateFormat("E, dd MM yyyy HH:mm:ss z");

    public HeaderGenerator(String ext, String filePath, Request req){
        this.ext = ext;
        this.filePath = filePath;
        this.req = req;
    }

    public void generate(HashMap<String,String> headers){
        headers.put("Date",df.format(new Date()));
        headers.put("Connection",(req.getKeepAlive()? "keep-live" : "close"));
        if (req.getHeaders().containsKey("Origin"))
            headers.put("Access-Control-Allow-Credentials", String.valueOf(HTAccess.getInstance().isCredentialsAllowed(req.getHost())));
    }

    public void generate(HashMap<String,String> headers, long bodyLength){
        headers.put("Date",df.format(new Date()));
        headers.put("Connection",(req.getKeepAlive()? "keep-live" : "close"));
        if (req.getHeaders().containsKey("Origin"))
            headers.put("Access-Control-Allow-Credentials", String.valueOf(HTAccess.getInstance().isCredentialsAllowed(req.getHost())));
        if (req.getMethod() != Methods.HEAD){
            headers.put("Content-Length",String.valueOf(bodyLength));
            headers.put("Content-Type", (ext.equals(".msghtml") ? "message/html" : FileTypes.getContentType(ext,req.getHost())));
        }
    }
}
