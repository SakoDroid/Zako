package Server.API;

import Engines.Captcha.Captcha;
import Server.Reqandres.Request.*;
import Server.Reqandres.Senders.FileSender;
import Server.Utils.Compression.CompressorFactory;
import Server.Utils.Configs.HTAccess;
import Server.Utils.Logger;
import java.io.File;

public class CaptchaSender implements API{
    @Override
    public void init(Request req) {
        Captcha cp = new Captcha(req.getIP(),req.getHost());
        FileSender fs = new FileSender(req.getProt(),200);
        fs.setContentType("image/png");
        fs.setKeepAlive(true);
        if (req.shouldBeCompressed() && HTAccess.getInstance().isCompressionAllowed(req.getHost())){
            Logger.glog("Client requested compression by " + req.getCompressionAlg() + " algorithm. Response data will be compressed."
                    + "  ; debug_id = " + req.getID(), req.getHost());
            File toBeSent = new CompressorFactory().getCompressor(req.getCompressionAlg()).compress(cp.image,req.getID());
            fs.addHeader("Content-Encoding: " + req.getCompressionAlg());
            fs.sendFile(toBeSent,req);
        }else
            fs.sendFile(cp.image,req);
        cp.image = null;
    }
}
