package Engines.CGIClient;

import Engines.CGI;
import Server.Reqandres.Request.ServerRequest;
import Server.Reqandres.Senders.FileSender;
import Server.Utils.*;
import java.io.*;
import java.util.*;

public class CGIProcess extends CGI {

    private List<String> commands;

    public CGIProcess(String ext, File cgiFile, ServerRequest req){
        this.extension = ext;
        this.file = cgiFile;
        this.req = req;
        this.getCMD();
        this.getParams();
    }

    public CGIProcess(String ext, File cgiFile, ServerRequest req, String args){
        this.extension = ext;
        this.file = cgiFile;
        this.req = req;
        this.getCMD();
        commands.add(args);
        this.getParams();
    }

    public CGIProcess(File cgiFile, ServerRequest req, List<String> commands){
        this.extension = "NA";
        this.file = cgiFile;
        this.req = req;
        this.commands = commands;
        this.getParams();
    }

    @Override
    public void exec(byte[] body,boolean ka){
        execCGI(body,ka);
    }

    private void execCGI(byte[] body, boolean ka){
        try{
            ProcessBuilder pb = new ProcessBuilder(commands);
            Logger.CGILog("Preparing the environment => adding envs. ; id = " + req.getID(),file.getName(),req.getHost());
            Methods mthd = req.getMethod();
            pb.environment().putAll(this.envs);
            Process p = pb.start();
            Logger.CGILog("Process created => running code ... ; id = " + req.getID() + "  ; PID = " + p.pid(),file.getName(),req.getHost());
            if (mthd == Methods.POST){
                OutputStream osw = p.getOutputStream();
                Logger.CGILog("Process created => Injecting post body ... ; id = " + req.getID() + "  ; PID = " + p.pid(),file.getName(),req.getHost());
                osw.write(body);
                osw.flush();
                osw.close();
            }
            String err = new String(p.getErrorStream().readAllBytes());
            if (err.isEmpty()){
                Logger.CGILog("Process created => Extracting result ...  ; id = " + req.getID()+ "  ; PID = " + p.pid(),file.getName(), req.getHost());
                CGIDataSender ds = new CGIDataSender(req.getProt(),200,p.getInputStream());
                ds.setKeepAlive(ka);
                ds.send(req.out,req.getIP(),req.getID(),req.getHost());
                Logger.CGILog("Process Finished => All done! OK ; id = " + req.getID()+ "  ; PID = " + p.pid(),file.getName(),req.getHost());
            }else{
                FileSender fs = new FileSender(req.getProt(),200);
                fs.setContentType("text/plain");
                fs.setKeepAlive(ka);
                fs.send(err,req.out,req.getIP(),req.getID(),req.getHost());
                Logger.CGIError(err + " ; id = " + req.getID()+ "  ; PID = " + p.pid(),file.getName(),req.getHost());
                Logger.CGILog("Process Finished => Error. (Check CGI-Logs for error info) ; id = " + req.getID()+ "  ; PID = " + p.pid(),file.getName(),req.getHost());
            }
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    private void getCMD(){
        commands = new ArrayList<>();
        if (extension.equals("php"))
            commands.add("php");
        commands.add(file.getAbsolutePath());
    }
}
