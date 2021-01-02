package Engines.FastCGI;

import Server.Reqandres.Request;
import Server.Reqandres.Senders.FileSender;
import Server.Utils.*;
import java.io.*;
import java.util.*;

public class CGIProcess {

    private final String extension;
    private File file;
    private final Request req;
    private boolean executable;
    private List<String> commands;

    public CGIProcess(String ext,File cgifile,Request req){
        this.extension = ext;
        this.file = cgifile;
        this.req = req;
        this.getCMD();
    }

    public void exec(String body,boolean ka){
        if (executable) execCGI(body,ka);
        else sendPlain(ka);
    }

    private void execCGI(String body, boolean ka){
        try{
            ProcessBuilder pb = new ProcessBuilder(commands);
            Logger.CGILog("Preparing the environment => adding envs. ; id = " + req.getID(),file.getName(),req.getHost());
            Methods mthd = req.getMethod();
            String query = req.getURL().getQuery();
            if (query != null) pb.environment().put("QUERY_STRING", query);
            String ck = (String)req.getHeaders().get("Cookie");
            if (ck != null) pb.environment().put("HTTP_COOKIE",ck);
            pb.environment().put("HTTP_USER_AGENT",(String)req.getHeaders().get("User-Agent"));
            pb.environment().put("PATH_INFO",req.getURL().getPath());
            pb.environment().put("REQUEST_METHOD",String.valueOf(mthd));
            pb.environment().put("SCRIPT_FILENAME", Configs.getCGIDir(req.getHost()) + req.getURL().getPath());
            pb.environment().put("SCRIPT_NAME",file.getName());
            pb.environment().put("SERVER_SOFTWARE","Zako 0.1");
            pb.environment().put("REMOTE_ADDR",req.getIP());
            if (mthd == Methods.POST){
                pb.environment().put("CONTENT_TYPE",(String)req.getHeaders().get("Content-Type"));
                pb.environment().put("CONTENT_LENGTH",(String)req.getHeaders().get("Content-Length"));
            }
            Process p = pb.start();
            Logger.CGILog("Process created => running code ... ; id = " + req.getID() + "  ; PID = " + p.pid(),file.getName(),req.getHost());
            InputStream errin = p.getErrorStream();
            if (mthd == Methods.POST){
                OutputStreamWriter osw = new OutputStreamWriter(p.getOutputStream());
                Logger.CGILog("Process created => Injecting post body ... ; id = " + req.getID() + "  ; PID = " + p.pid(),file.getName(),req.getHost());
                osw.write(body);
                osw.flush();
            }
            int i;
            String err = "";
            while((i = errin.read()) != -1){
                err += (char)i;
            }
            if (err.isEmpty()){
                Logger.CGILog("Process created => Extracting result ...  ; id = " + req.getID()+ "  ; PID = " + p.pid(),file.getName(), req.getHost());
                CGIDataSender ds = new CGIDataSender(req.getProt(),200,p.getInputStream());
                ds.setKeepAlive(ka);
                ds.send(req.out,req.getIP(),req.getID(),req.getHost());
                Logger.CGILog("Process Finished => All done! OK ; id = " + req.getID()+ "  ; PID = " + p.pid(),file.getName(),req.getHost());
            }else{
                FileSender fs = new FileSender(req.getProt(),204);
                fs.setContentType("text/plain");
                fs.setKeepAlive(ka);
                fs.send(err,req.out,req.getIP(),req.getID(),req.getHost());
                Logger.CGIError(err + " ; id = " + req.getID()+ "  ; PID = " + p.pid(),file.getName(),req.getHost());
                Logger.CGILog("Process Finished => Error. (Check CGI-Logs for error info) ; id = " + req.getID()+ "  ; PID = " + p.pid(),file.getName(),req.getHost());
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

    private void sendPlain(boolean ka){
        file = new File(Configs.getMainDir(req.getHost()) + req.Path);
        FileSender fs = new FileSender(req.getProt(),200);
        fs.setContentType("text/plain");
        fs.setExtension(extension);
        fs.setKeepAlive(ka);
        fs.sendFile(req.getMethod(), file, req.out, req.getIP(), req.getID(), req.getHost());
    }

    private void getCMD(){
        String cmd = basicUtils.getExecCmd(extension);
        if (cmd != null){
            executable = true;
            commands = new ArrayList<>();
            commands.add(cmd);
            commands.add(file.getAbsolutePath());
        } else executable = false;
    }
}
