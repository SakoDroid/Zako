package Server.Reqandres.Senders;

import Server.Reqandres.Request.Request;
import Server.Utils.Configs.FileTypes;
import Server.Utils.Configs.HTAccess;
import Server.Utils.Configs.SSLConfigs;
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
        if (SSLConfigs.isSSLOn(req.getHost()) && SSLConfigs.isHTTPSOnly(req.getHost()))
            this.addStrictTransportSecurityHeader(headers);
        if (req.getHeaders().containsKey("Origin"))
            headers.put("Access-Control-Allow-Credentials", String.valueOf(HTAccess.getInstance().isCredentialsAllowed(req.getHost())));
        this.addUserDefinedHeaders(headers);
    }

    public void generate(HashMap<String,String> headers, long bodyLength,boolean mp){
        headers.put("Date",df.format(new Date()));
        headers.put("Connection",(req.getKeepAlive()? "keep-live" : "close"));
        headers.put("Accept-Ranges",(HTAccess.getInstance().isRangesAccepted(req.getHost()) ? "bytes" : "none"));
        if (req.getHeaders().containsKey("Origin"))
            headers.put("Access-Control-Allow-Credentials", String.valueOf(HTAccess.getInstance().isCredentialsAllowed(req.getHost())));
        if (req.getMethod() != Methods.HEAD){
            headers.put("Content-Length",String.valueOf(bodyLength));
            if (req.getRanges().size() < 2)
                headers.put("Content-Type", (ext.equals(".msghtml") ? "message/html" : FileTypes.getContentType(ext,req.getHost())));
            else
                headers.put("Content-Type", "multipart/byteranges ; boundary=" + req.getBoundary());
        }
        this.addUserDefinedHeaders(headers);
    }

    private void addUserDefinedHeaders(HashMap<String,String> headers){
        if (ext != null) {
            HashMap<String,String> hds = FileTypes.getHeaders(this.ext,req.getHost());
            if (hds != null)
                headers.putAll(hds);
        }
        if (filePath != null){
            HashMap<String,String> hds = FileTypes.getHeaders(this.filePath,req.getHost());
            if (hds != null)
                headers.putAll(hds);
        }
    }

    private void addStrictTransportSecurityHeader(HashMap<String,String> headers){
        String val = "max-age=";
        val += SSLConfigs.getMaxAge(req.getHost()) + "; ";
        if (SSLConfigs.isSubdomainIncluded(req.getHost()))
            val += "includeSubDomains";
        headers.put("Strict-Transport-Security",val);
    }
}
