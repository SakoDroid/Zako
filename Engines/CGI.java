package Engines;

import Server.Reqandres.Request;
import Server.Utils.Configs;
import Server.Utils.Methods;

import java.io.File;
import java.util.*;

public abstract class CGI {

    protected String extension;
    protected File file;
    protected Request req;
    protected Map<String,String> envs;

    public abstract void exec(String body,boolean ka);

    public void getParams(){
        envs = new HashMap<>();
        Methods mthd = req.getMethod();
        String query = req.getURL().getQuery();
        if (query != null) envs.put("QUERY_STRING", query);
        String ck = (String)req.getHeaders().get("Cookie");
        if (ck != null) envs.put("HTTP_COOKIE",ck);
        envs.put("HTTP_USER_AGENT",(String)req.getHeaders().get("User-Agent"));
        envs.put("PATH_INFO",req.getURL().getPath());
        envs.put("REQUEST_METHOD",String.valueOf(mthd));
        envs.put("SCRIPT_FILENAME", Configs.getCGIDir(req.getHost()) + req.getURL().getPath());
        envs.put("SCRIPT_NAME",file.getName());
        envs.put("SERVER_SOFTWARE","Zako 0.1");
        envs.put("REMOTE_ADDR",req.getIP());
        if (mthd == Methods.POST){
            envs.put("CONTENT_TYPE",(String)req.getHeaders().get("Content-Type"));
            envs.put("CONTENT_LENGTH",(String)req.getHeaders().get("Content-Length"));
        }
    }
}
