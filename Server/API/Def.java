package Server.API;

import Server.Reqandres.Request.*;
import Server.Reqandres.Senders.*;
import Server.Utils.Configs.Configs;
import Server.Utils.Configs.FileTypes;
import java.io.File;
import java.util.regex.*;

public class Def implements API{
    @Override
    public void init(Request req) {
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
                        new QuickSender(req).sendFile(fl, ext);
                    }
                } else {
                    fl = new File(Configs.getMainDir(req.getHost()) + req.getPath());
                    new QuickSender(req).sendFile(fl, ".bin");
                }
            } else {
                fl = new File(Configs.getCGIDir(req.getHost()) + req.getPath());
                if (fl.exists()){
                    req.convertBody();
                    new ScriptHandler(req, ext).process(req.getConvertedBody(),req.getKeepAlive());
                }
                else {
                    fl = new File(Configs.getMainDir(req.getHost()) + req.getPath());
                    new QuickSender(req).sendFile(fl, ext);
                }
            }
        }else {
            fl = new File(Configs.getMainDir(req.getHost()) + req.getPath());
            new QuickSender(req).sendFile(fl, ext);
        }
    }
}
