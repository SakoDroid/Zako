package Server.Reqandres.CGI;

import Server.Reqandres.Senders.FileSender;
import Server.Utils.Configs;
import Server.Utils.Logger;
import Server.Utils.Methods;
import java.io.*;
import java.util.*;
import java.net.URL;

public class CGIExecuter {

    private final File cgiFile;
    private final List<String> commands;
    private final HashMap headers;
    private final URL url;
    private final DataOutputStream out;

    public CGIExecuter(List<String> cmds,File cgifile,int id,String Host,HashMap hd,URL ur,DataOutputStream osw,String CGIBoody,String ip,boolean ka){
        this.cgiFile = cgifile;
        this.commands = cmds;
        this.headers = hd;
        this.url = ur;
        this.out = osw;
        this.exec(CGIBoody,ip,id,Host,ka);
    }

    private void exec(String body,String ip,int id,String Host,boolean ka){
        try{
            ProcessBuilder pb = new ProcessBuilder(commands);
            Logger.CGILog("Preparing the environment => adding envs. ; id = " + id,cgiFile.getName(),Host);
            Methods mthd = (Methods)headers.get("Method");
            String query = url.getQuery();
            if (query != null) pb.environment().put("QUERY_STRING", query);
            String ck = (String)headers.get("Cookie");
            if (ck != null) pb.environment().put("HTTP_COOKIE",ck);
            pb.environment().put("HTTP_USER_AGENT",(String)headers.get("User-Agent"));
            pb.environment().put("PATH_INFO",url.getPath());
            pb.environment().put("REQUEST_METHOD",String.valueOf(mthd));
            pb.environment().put("SCRIPT_FILENAME", Configs.getCGIDir(Host) + url.getPath());
            pb.environment().put("SCRIPT_NAME",cgiFile.getName());
            pb.environment().put("SERVER_SOFTWARE","Zako 0.1");
            pb.environment().put("REMOTE_ADDR",ip);
            if (mthd == Methods.POST){
                pb.environment().put("CONTENT_TYPE",(String)headers.get("Content-Type"));
                pb.environment().put("CONTENT_LENGTH",(String)headers.get("Content-Length"));
            }
            Process p = pb.start();
            Logger.CGILog("Process created => running code ... ; id = " + id + "  ; PID = " + p.pid(),cgiFile.getName(),Host);
            InputStream errin = p.getErrorStream();
            if (mthd.equals("POST")){
                OutputStreamWriter osw = new OutputStreamWriter(p.getOutputStream());
                Logger.CGILog("Process created => Injecting post body ... ; id = " + id + "  ; PID = " + p.pid(),cgiFile.getName(),Host);
                osw.write(body);
                osw.flush();
            }
            int i;
            String err = "";
            while((i = errin.read()) != -1){
                err += (char)i;
            }
            if (err.isEmpty()){
                Logger.CGILog("Process created => Extracting result ...  ; id = " + id+ "  ; PID = " + p.pid(),cgiFile.getName(),Host);
                CGIDataSender ds = new CGIDataSender((String)headers.get("Version"),200,p.getInputStream());
                ds.setKeepAlive(ka);
                ds.send(out,ip,id,Host);
                Logger.CGILog("Process Finished => All done! OK ; id = " + id+ "  ; PID = " + p.pid(),cgiFile.getName(),Host);
            }else{
                FileSender fs = new FileSender((String)headers.get("Version"),204);
                fs.setContentType("text/plain");
                fs.setKeepAlive(ka);
                fs.send(err,out,ip,id,Host);
                Logger.CGIError(err + " ; id = " + id+ "  ; PID = " + p.pid(),cgiFile.getName(),Host);
                Logger.CGILog("Process Finished => Error. (Check CGI-Logs for error info) ; id = " + id+ "  ; PID = " + p.pid(),cgiFile.getName(),Host);
            }
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }
}