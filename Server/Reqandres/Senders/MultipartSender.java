package Server.Reqandres.Senders;

import Server.Reqandres.Request.Request;
import Server.Utils.Configs.FileTypes;
import Server.Utils.Logger;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

public class MultipartSender extends Sender {

    private int responseCode = 206;
    private final HashMap<long[], String> multipartHeaders = new HashMap<>();
    private long length = 0;

    public MultipartSender(String proto) {
        super(proto, 206);
    }

    private void setResponseCode(){
        this.setStatus(416);
        this.responseCode = 416;
    }

    private String generateHeaders(Request req, String filePath, long bodyLength) {
        new HeaderGenerator(this.ext, filePath, req).generate(this.headers, bodyLength, true);
        return super.turnHeadersIntoString(req.getProt());
    }

    private void generateMultipartHeaders(ArrayList<long[]> ranges, String boundary,long fileLength) {
        if (ranges.size() > 1){
            for (long[] range : ranges) {
                long len;
                long off;
                long end;
                if (range[0] == -1) {
                    len = range[1];
                    off = fileLength - range[1];
                    end = fileLength - 1;
                } else {
                    if (range[1] > fileLength)
                        this.setResponseCode();
                    len = (range[1] - range[0] + 1);
                    off = range[0];
                    end = range[1];
                }
                String header = "--" + boundary;
                header += "\r\nContent-Type: " + this.contentType + "\r\nContent-Range: bytes " + off + "-" + end + "/" + len + "\r\n\r\n";
                length += len + header.getBytes().length;
                length += boundary.getBytes().length + 6 + (range.length - 1) * 2L;
                multipartHeaders.put(range, header);
            }
        }else{
            long[] data = ranges.get(0);
            if (data[1] > fileLength)
                this.setResponseCode();
            else{
                multipartHeaders.put(data,"");
                if (data[0] == -1)
                    length = data[1];
                else
                    length = data[1] - data[0];
            }
        }
    }

    public void send(File fl, Request req) {
        try {
            this.contentType = FileTypes.getContentType(ext, req.getHost());
            Logger.glog(req.getIP() + " has requested partial content of " + fl.getName() + ". preparing to send ...    ;     debug_id = " + req.getID(), req.getHost());
            this.generateMultipartHeaders(req.getRanges(), req.getBoundary(), fl.length());
            if (this.responseCode != 416){
                req.getOutStream().writeBytes(this.generateHeaders(req, fl.getAbsolutePath(), length));
                RandomAccessFile raf = new RandomAccessFile(fl, "r");
                boolean firstOnePassed = false;
                for (long[] range : multipartHeaders.keySet()) {
                    if (firstOnePassed)
                        req.getOutStream().write("\r\n".getBytes());
                    else
                        firstOnePassed = true;
                    req.getOutStream().writeBytes(multipartHeaders.get(range));
                    long offset;
                    long length;
                    long read = 0;
                    if (range[0] == -1) {
                        offset = raf.length() - range[1];
                        length = range[1];
                    } else {
                        offset = range[0];
                        length = (range[1] - range[0]);
                    }
                    byte[] buffer = new byte[1024];
                    raf.seek(offset);
                    while (read < length) {
                        int toBeRead;
                        if (read + 1024 < length)
                            toBeRead = 1024;
                        else
                            toBeRead = (int) ((int) length - read);
                        if (toBeRead + read > raf.length())
                            toBeRead = (int) (raf.length() - read);
                        int readAmount = raf.read(buffer, 0, toBeRead);
                        if (readAmount == -1)
                            break;
                        read += readAmount;
                        req.getOutStream().write(buffer);
                    }
                }
                if (req.getRanges().size() > 1)
                    req.getOutStream().writeBytes("\r\n--" + req.getBoundary() + "--");
                if (!req.getKeepAlive()) {
                    req.getOutStream().flush();
                    req.getOutStream().close();
                }
            }else
                new QuickSender(req).sendCode(416);
        } catch (Exception ex) {
            Logger.logException(ex);
        }
    }
}