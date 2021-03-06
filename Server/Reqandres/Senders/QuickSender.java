package Server.Reqandres.Senders;

import Server.Reqandres.Request.Request;
import Server.Utils.Compression.CompressorFactory;
import Server.Utils.Configs.*;
import Server.Utils.HeaderRelatedTools.*;
import Server.Utils.Logger;
import java.io.File;

public class QuickSender {

    private final Request req;

    public QuickSender(Request req){
        this.req = req;
    }

    public void sendCode(int code){
        if (code > 400){
            FileSender fs = new FileSender(req.getProt(), code);
            fs.setKeepAlive(req.getKeepAlive());
            fs.setExtension(".html");
            fs.sendFile(new File(Configs.getCWD() + "/default_pages/" + code + ".html"), req);
        }else{
            this.sendBadReq(req.getErrorReason());
        }
    }

    public void sendBadReq(String reason){
        Logger.glog("Sending back bad request (400) response to " + req.getIP() + ", reason : " + reason + "    ; debug_id = " + req.getID(),req.getHost());
        Sender snd = new Sender(req.getProt(),400);
        snd.setKeepAlive(req.getKeepAlive());
        snd.setExtension(".txt");
        snd.send(reason,req);
    }

    public void redirect(int redirectCode,String location){
        if (redirectCode > 299 && redirectCode < 309){
            Logger.glog("Redirecting " + req.getIP() + " to " + location + "  ; id = " + req.getID(),req.getHost());
            Sender snd = new Sender(req.getProt(),redirectCode);
            snd.setKeepAlive(false);
            snd.setExtension(".txt");
            snd.addHeader("Location",location);
            snd.send(null,req);
        }else
            throw new IllegalArgumentException("Response code " + redirectCode + " is not valid for redirection.");
    }

    public void sendFile(File fl,String ext){
        if (fl.isFile()) {
            String MIMEType = FileTypes.getContentType(ext,req.getHost());
            if (req.isMIMEAcceptable(MIMEType)) {
                if (req.getRanges().isEmpty()){
                    FileSender fs = new FileSender(req.getProt(), 200);
                    fs.setExtension(ext);
                    fs.setKeepAlive(req.getKeepAlive());
                    if (HTAccess.getInstance().shouldETagBeSent(fl.getAbsolutePath(), req.getHost()))
                        fs.addHeader("ETag", "\"" + new HashComputer(fl).computeHash() + "\"");
                    if (HTAccess.getInstance().shouldLMBeSent(fl.getAbsolutePath(), req.getHost()))
                        fs.addHeader("Last-Modified", new LMGenerator(fl).generate());
                    if (req.shouldBeCompressed() && HTAccess.getInstance().isCompressionAllowed(req.getHost()) && fl.length() < 50000000) {
                        Logger.glog("Client requested compression by " + req.getCompressionAlg() + " algorithm. Response data will be compressed."
                                + "  ; debug_id = " + req.getID(), req.getHost());
                        fl = new CompressorFactory().getCompressor(req.getCompressionAlg()).compress(fl);
                        fs.addHeader("Content-Encoding", String.valueOf(req.getCompressionAlg()));
                    }
                    fs.sendFile(fl, req);
                }else{
                    MultipartSender mps = new MultipartSender(req.getProt());
                    mps.setExtension(ext);
                    mps.setKeepAlive(req.getKeepAlive());
                    mps.send(fl,req);
                }
            }else
                this.sendCode(406);
        } else
            this.sendCode(404);
    }
}
