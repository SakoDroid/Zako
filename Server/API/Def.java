package Server.API;

import Server.Reqandres.CGI.CGIExecuter;
import Server.Reqandres.Request;
import Server.Reqandres.RequestProcessor;
import Server.Reqandres.Senders.FileSender;
import Server.Utils.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class Def implements API{
    @Override
    public void init(Request req, RequestProcessor reqp) {
        Pattern pt = Pattern.compile("\\.\\w+");
        Matcher mc = pt.matcher(req.Path);
        String ext = "";
        if (mc.find()) ext = mc.group();
        File fl;
        String tempCntc = FileTypes.getContentType(ext);
        if (tempCntc != null) {
            if (tempCntc.equals("CGI") || tempCntc.contains("java")) {
                fl = new File(Configs.getCGIDir(req.getHost()) + req.Path);
                String cmd = basicUtils.getExecCmd(ext.replace(".", ""));
                if (cmd != null && fl.exists()) {
                    Logger.glog("CGI script requested. preparing for executing ...   ; id = " + req.getID(), req.getHost());
                    List<String> cmds = new ArrayList<>();
                    cmds.add(cmd);
                    cmds.add(fl.getAbsolutePath());
                    new CGIExecuter(cmds, fl, req.getID(), req.getHost(), reqp.headers, req.getURL(), req.out, reqp.Body, req.getIP());
                } else {
                    fl = new File(Configs.getMainDir(req.getHost()) + req.Path);
                    FileSender.setProt(req.getProt());
                    if (ext.equals(".js")) FileSender.setContentType(FileTypes.getContentType(".js"));
                    else FileSender.setContentType("text/plain");
                    FileSender.setStatus(200);
                    FileSender.sendFile(req.getMethod(), fl, req.out, req.getIP(), req.getID(), req.getHost());
                }
            } else {
                fl = new File(Configs.getMainDir(req.getHost()) + req.Path);
                if (fl.isFile()) {
                    FileSender.setProt(req.getProt());
                    FileSender.setContentType(tempCntc);
                    FileSender.setStatus(200);
                    FileSender.sendFile(req.getMethod(), fl, req.out, req.getIP(), req.getID(), req.getHost());
                } else basicUtils.sendCode(404,req);
            }
        } else {
            fl = new File(Configs.getMainDir(req.getHost()) + req.Path);
            if (fl.isFile()) {
                FileSender.setProt(req.getProt());
                FileSender.setContentType(FileTypes.getContentType(".bin"));
                FileSender.setStatus(200);
                FileSender.sendFile(req.getMethod(), fl, req.out, req.getIP(), req.getID(), req.getHost());
            } else basicUtils.sendCode(404,req);
        }
    }
}
