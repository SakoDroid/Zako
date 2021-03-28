package Engines;

import Engines.FCGI.Client.Utils.Utils;
import Server.Reqandres.Request.Request;
import Server.Utils.Configs.Configs;
import Server.Utils.Enums.Methods;
import Server.Utils.basicUtils;
import java.io.File;
import java.util.*;

public abstract class CGI {

    protected boolean FCGI = false;
    protected String extension;
    protected File file;
    protected Request req;
    protected Map<String,String> envs;

    public abstract void exec(byte[] body,boolean ka);

    public void getParams(){
        envs = new HashMap<>();
        Methods mthd = req.getMethod();
        String query = req.getURL().getQuery();
        String ck = (String)req.getHeaders().get("Cookie");
        if (FCGI) envs.put("GATEWAY_INTERFACE", "FastCGI/1.0");
        envs.put("QUERY_STRING", ((query != null) ? query : ""));
        envs.put("PATH_INFO",req.getOrgPath());
        envs.put("DOCUMENT_ROOT", file.getAbsolutePath().replace("/" + file.getName(),""));
        envs.put("HTTP_COOKIE",((ck != null) ? ck : ""));
        envs.put("HTTP_USER_AGENT",(String)req.getHeaders().get("User-Agent"));
        envs.put("REQUEST_METHOD",String.valueOf(mthd));
        envs.put("REQUEST_URI", req.getPath());
        envs.put("SCRIPT_FILENAME", file.getAbsolutePath());
        envs.put("SCRIPT_NAME",file.getName());
        envs.put("REMOTE_ADDR",req.getIP());
        envs.put("REMOTE_PORT", "9985");
        envs.put("SERVER_SOFTWARE",basicUtils.Zako);
        envs.put("SERVER_ADDR", basicUtils.LocalHostIP.trim());
        envs.put("SERVER_NAME", req.getHost());
        envs.put("SERVER_PORT", String.valueOf(Configs.getPorts().get(req.getHost())));
        envs.put("SERVER_PROTOCOL", req.getProt());
        if (mthd == Methods.POST)
            envs.put("CONTENT_TYPE",(String)req.getHeaders().get("Content-Type"));
        Utils.fixEnvs(envs);
    }
}
