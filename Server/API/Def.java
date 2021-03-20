package Server.API;

import Engines.CGIClient.CGIProcess;
import Server.Reqandres.Request.Request;
import Server.Reqandres.Request.RequestProcessor;
import Server.Reqandres.Senders.FileSender;
import Server.Utils.*;
import java.io.File;
import java.util.regex.*;

public class Def implements API{
    @Override
    public void init(Request req, RequestProcessor reqp) {
        Pattern pt = Pattern.compile("\\.\\w+");
        Matcher mc = pt.matcher(req.Path);
        String ext = "";
        if (mc.find()) ext = mc.group();
        File fl;
        if (!ext.isEmpty()){
            if (!ext.equals(".js")) {
                String tempCntc = FileTypes.getContentType(ext,req.getHost());
                if (tempCntc != null) {
                    if (tempCntc.equals("CGI")) {
                        new ScriptHandler(req, ext).process(basicUtils.toByteArray(reqp.Body),reqp.KA);
                    } else {
                        fl = new File(Configs.getMainDir(req.getHost()) + req.Path);
                        sendFile(fl, ext, req, reqp.KA);
                    }
                } else {
                    fl = new File(Configs.getMainDir(req.getHost()) + req.Path);
                    sendFile(fl, ".bin", req, reqp.KA);
                }
            } else {
                fl = new File(Configs.getCGIDir(req.getHost()) + req.Path);
                if (fl.exists())
                    new CGIProcess(ext, fl, req).exec(basicUtils.toByteArray(reqp.Body), reqp.KA);
                else {
                    fl = new File(Configs.getMainDir(req.getHost()) + req.Path);
                    sendFile(fl, ext, req, reqp.KA);
                }
            }
        }else {
            fl = new File(Configs.getMainDir(req.getHost()) + req.Path);
            if (fl.isFile())
                this.sendFile(fl,ext,req, reqp.KA);
            else
                basicUtils.sendCode(404,req);
        }
    }

    private void sendFile(File fl,String ext,Request req,boolean ka){
        if (fl.isFile()) {
            FileSender fs = new FileSender(req.getProt(), 200);
            fs.setContentType(FileTypes.getContentType(ext,req.getHost()));
            fs.setExtension(ext);
            fs.setKeepAlive(ka);
            fs.sendFile(req.getMethod(), fl, req.out, req.getIP(), req.getID(), req.getHost());
        } else
            basicUtils.sendCode(404, req);
    }
}
