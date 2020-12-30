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
                String cmd = basicUtils.getExecCmd(ext);
                if (cmd != null && fl.exists()) {
                    Logger.glog("CGI script requested. preparing for executing ...   ; id = " + req.getID(), req.getHost());
                    List<String> cmds = new ArrayList<>();
                    cmds.add(cmd);
                    cmds.add(fl.getAbsolutePath());
                    new CGIExecuter(cmds, fl, req.getID(), req.getHost(), reqp.headers, req.getURL(), req.out, reqp.Body, req.getIP());
                } else {
                    fl = new File(Configs.getMainDir(req.getHost()) + req.Path);
                    FileSender fs = new FileSender(req.getProt(),200);
                    if (ext.equals(".js")) fs.setContentType(FileTypes.getContentType(".js"));
                    else fs.setContentType("text/plain");
                    fs.sendFile(req.getMethod(), fl, req.out, req.getIP(), req.getID(), req.getHost());
                }
            } else {
                fl = new File(Configs.getMainDir(req.getHost()) + req.Path);
                if (fl.isFile()) {
                    FileSender fs = new FileSender(req.getProt(),200);
                    fs.setContentType(tempCntc);
                    fs.sendFile(req.getMethod(), fl, req.out, req.getIP(), req.getID(), req.getHost());
                } else basicUtils.sendCode(404,req);
            }
        } else {
            fl = new File(Configs.getMainDir(req.getHost()) + req.Path);
            if (fl.isFile()) {
                FileSender fs = new FileSender(req.getProt(),200);
                fs.setContentType(FileTypes.getContentType(".bin"));
                fs.sendFile(req.getMethod(), fl, req.out, req.getIP(), req.getID(), req.getHost());
            } else basicUtils.sendCode(404,req);
        }
    }
}
