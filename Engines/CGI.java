package Engines;

import Engines.FCGI.Client.Utils.Utils;
import Server.Reqandres.Request;
import Server.Utils.Configs;
import Server.Utils.Methods;
import Server.Utils.basicUtils;
import java.io.File;
import java.util.*;

public abstract class CGI {

    protected boolean FCGI = false;
    protected String extension;
    protected File file;
    protected Request req;
    protected Map<String,String> envs;

    public abstract void exec(String body,boolean ka);

    public void getParams(){
        envs = new HashMap<>();
        Methods mthd = req.getMethod();
        String query = req.getURL().getQuery();
        String ck = (String)req.getHeaders().get("Cookie");
        if (FCGI) envs.put("GATEWAY_INTERFACE", "FastCGI/1.0");
        envs.put("QUERY_STRING", ((query != null) ? query : ""));
        envs.put("PATH_INFO",req.orgPath);
        envs.put("DOCUMENT_ROOT", Configs.getCGIDir(req.getHost()));
        envs.put("HTTP_COOKIE",((ck != null) ? ck : ""));
        envs.put("HTTP_USER_AGENT",(String)req.getHeaders().get("User-Agent"));
        envs.put("REQUEST_METHOD",String.valueOf(mthd));
        envs.put("REQUEST_URI", req.Path);
        envs.put("SCRIPT_FILENAME", Configs.getCGIDir(req.getHost()) + req.Path);
        envs.put("SCRIPT_NAME",file.getName());
        envs.put("REMOTE_ADDR",req.getIP());
        envs.put("REMOTE_PORT", "9985");
        envs.put("SERVER_SOFTWARE","Zako 0.1");
        envs.put("SERVER_ADDR", basicUtils.LocalHostIP.trim());
        envs.put("SERVER_NAME", "localhost");
        envs.put("SERVER_PORT", String.valueOf(Configs.getWSPort()));
        envs.put("SERVER_PROTOCOL", req.getProt());
        if (mthd == Methods.POST)
            envs.put("CONTENT_TYPE",(String)req.getHeaders().get("Content-Type"));
        Utils.fixEnvs(envs);
        System.out.println(envs);
    }
}
