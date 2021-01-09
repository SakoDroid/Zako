package Engines.FCGI.Client.Response;

import Engines.FCGI.Client.Utils.FCGIConstants;
import Server.Utils.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FCGIResponse {

    private FCGIEndRequestBody endRequest;
    private ArrayList<Byte> content = new ArrayList<>();
    private String errorContent = "";
    private final int reqID;
    public int status;

    public FCGIResponse(InputStream in, int requestID){
        this.reqID = requestID;
        this.read(in);
    }

    public FCGIEndRequestBody getEndRequest(){
        return this.endRequest;
    }

    public ArrayList<Byte> getContent(){
        return this.content;
    }

    public String getErrorContent(){
        return this.errorContent;
    }

    private void read(InputStream in){
        int res;
        do {
            res = readAResponse(in);
        }while (res > 0);
    }

    private int readAResponse(InputStream in){
        byte[] headers = new byte[8];
        int temp = 0;
        try{
            temp = in.read(headers);
            FCGIResponseHeader header = new FCGIResponseHeader(headers);
            switch (header.type) {
                case FCGIConstants.FCGI_END_REQUEST -> {
                    temp = 0;
                    endRequest = new FCGIEndRequestBody(header.contentLength, in);
                }
                case FCGIConstants.FCGI_STDOUT,
                        FCGIConstants.FCGI_STDERR -> {
                    if (header.contentLength > 0){
                        byte[] body = new byte[header.contentLength];
                        int read = in.read(body);
                        if (read == header.contentLength) {
                            if (header.type == FCGIConstants.FCGI_STDOUT) {
                                if (this.status != -1) this.status = FCGIConstants.FCGI_REP_OK;
                                for (byte b : body) content.add(b);
                            } else {
                                this.status = FCGIConstants.FCGI_REP_ERROR;
                                this.errorContent += new String(body);
                            }
                        } else {
                            temp = 0;
                            status = FCGIConstants.FCGI_REP_ERROR_CONTENT_LENGTH;
                        }
                    }
                }
            }
            if (header.paddingLength > 0)
                in.skip(header.paddingLength);

        }catch (IOException ex) {
            status = FCGIConstants.FCGI_REP_ERROR_IOEXCEPTION;
            System.out.println("exp : " + ex.toString());
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
        return temp;
    }

    @Override
    public String toString(){
        return "status : " + this.status + "\nContent : " + this.content + "\nerror : " + this.errorContent;
    }
}
