package Server.API;

import Server.Reqandres.Request.*;
import Server.Reqandres.Senders.*;
import Server.Utils.Configs.Configs;
import Server.Utils.Configs.FileTypes;
import Server.Utils.Configs.HTAccess;
import Server.Utils.Headers.*;
import java.io.File;
import java.util.regex.*;

public class Def implements API{
    @Override
    public void init(Request req, RequestProcessor reqp) {
        Pattern pt = Pattern.compile("\\.\\w+");
        Matcher mc = pt.matcher(req.getPath());
        String ext = "";
        if (mc.find()) ext = mc.group();
        File fl;
        if (!ext.isEmpty()){
            if (!ext.equals(".js")) {
                String tempCntc = FileTypes.getContentType(ext,req.getHost());
                if (tempCntc != null) {
                    if (tempCntc.equals("CGI")) {
                        req.convertBody();
                        new ScriptHandler(req, ext).process(req.getConvertedBody(),req.getKeepAlive());
                    } else {
                        fl = new File(Configs.getMainDir(req.getHost()) + req.getPath());
                        sendFile(fl, ext, req, req.getKeepAlive());
                    }
                } else {
                    fl = new File(Configs.getMainDir(req.getHost()) + req.getPath());
                    sendFile(fl, ".bin", req, req.getKeepAlive());
                }
            } else {
                fl = new File(Configs.getCGIDir(req.getHost()) + req.getPath());
                if (fl.exists()){
                    req.convertBody();
                    new ScriptHandler(req, ext).process(req.getConvertedBody(),req.getKeepAlive());
                }
                else {
                    fl = new File(Configs.getMainDir(req.getHost()) + req.getPath());
                    sendFile(fl, ext, req, req.getKeepAlive());
                }
            }
        }else {
            fl = new File(Configs.getMainDir(req.getHost()) + req.getPath());
            if (fl.isFile())
                this.sendFile(fl,ext,req, req.getKeepAlive());
            else
                new QuickSender(req).sendCode(404);
        }
    }

    private void sendFile(File fl, String ext, Request req, boolean ka){
        if (fl.isFile()) {
            FileSender fs = new FileSender(req.getProt(), 200);
            fs.setContentType(FileTypes.getContentType(ext,req.getHost()));
            fs.setExtension(ext);
            fs.setKeepAlive(ka);
            if (HTAccess.getInstance().shouldETagBeSent(fl.getAbsolutePath(),req.getHost()))
                fs.addHeader("ETag: \"" + new HashComputer(fl).computeHash() + "\"");
            if (HTAccess.getInstance().shouldLMBeSent(fl.getAbsolutePath(),req.getHost()))
                fs.addHeader("Last-Modified: " + new LMGenerator(fl).generate());
            fs.sendFile(fl,req);
        } else
            new QuickSender(req).sendCode(404);
    }
}
