package Server.Reqandres.Request;

import Server.Utils.*;
import Server.Utils.Compression.Algorithm;
import Server.Utils.Configs.Configs;
import Server.Utils.Configs.HTAccess;
import Server.Utils.Enums.Methods;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;

public class Request {

    private String id;
    private String ip;
    private String fullip;
    private String Host = "default";
    private String Prot = "HTTP/1.1";
    private String Path;
    private String orgPath;
    private String errorReason;
    private Socket sck;
    private File TempFile;
    private URL url;
    private Methods method;
    private HashMap<String,String> headers = new HashMap<>();
    public DataOutputStream out;
    public InputStream is;
    private ArrayList<Byte> body = new ArrayList<>();
    private ArrayList<String> accepts = new ArrayList<>();
    private byte[] convertedBody = null;
    private boolean keepAlive = false;
    private boolean compression;
    private Algorithm compressionAlg;
    private int responseCode = 200;

    public Request(Socket client){
        this.id = java.util.UUID.randomUUID().toString();
        this.sck = client;
        this.ip = client.getInetAddress().getHostAddress();
        this.fullip = client.getRemoteSocketAddress().toString();
        this.TempFile = new File(Configs.getCWD() + "/Cache/Temp/temp" + id + ".tmp");
        SocketsData.getInstance().addRequest(client);
        this.setHost("default");
        SocketsData.getInstance().setMaxReqsPerSock(this.sck,HTAccess.getInstance().getMNORPC(this.Host));
        try{
            this.is = client.getInputStream();
            this.out = new DataOutputStream(client.getOutputStream());
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    public void addHeader(String key, String value){
        headers.put(key,value);
    }

    public void setHeaders(HashMap<String,String> hd){
        this.headers = hd;
    }

    public HashMap<String,String> getHeaders(){
        return this.headers;
    }

    public void setURL(URL ur){
        this.url = ur;
    }

    public URL getURL(){
        return this.url;
    }

    public void setMethod(Methods method){
        this.method = method;
    }

    public Methods getMethod(){
        return this.method;
    }

    public void setTimeout(int timeout){
        try {
            this.sck.setSoTimeout(timeout);
        }catch (Exception ex){
            Logger.logException(ex);
        }
    }

    public void setErrorReason(String errorReason){
        this.errorReason = errorReason;
    }

    public String getErrorReason() {
        return this.errorReason;
    }

    public void setProt(String prot){
        if (prot.equalsIgnoreCase("http/1.1"))
            this.Prot = "HTTP/1.1";
        else if (prot.equals("h2c") || prot.equals("h2"))
            this.Prot = "HTTP/2";
    }

    public void setHost(String host){
        if (this.Host.equals("default") && Configs.isHostAvailable(host)) {
            this.Host = URLDecoder.decode(host, StandardCharsets.UTF_8);
            SocketsData.getInstance().setMaxReqsPerSock(this.sck, HTAccess.getInstance().getMNORPC(this.Host));
        }
    }

    public void setPath(String path){
        this.Path = URLDecoder.decode(path.trim(), StandardCharsets.UTF_8);
    }

    public void setOrgPath(String path){
        this.orgPath = URLDecoder.decode(path.trim(), StandardCharsets.UTF_8);
    }

    public void setKeepAlive(boolean ka){
        this.keepAlive = ka;
    }

    public void setCompression(boolean cmp){
        this.compression = cmp;
    }

    public void setCompressionAlg(Algorithm alg){
        this.compressionAlg = alg;
    }

    public void setResponseCode(int code){
        this.responseCode = code;
    }

    public void addToBody(byte[] bt){
        for (byte b : bt)
            this.body.add(b);
    }

    public void addToBody(String bt){
        for (byte b : bt.getBytes())
            this.body.add(b);
    }

    public String getPath(){
        return this.Path;
    }

    public String getOrgPath(){
        return this.orgPath;
    }

    public String getHost(){
        return this.Host;
    }

    public String getID(){
        return this.id;
    }

    public String getIP(){
        return this.ip;
    }

    public DataOutputStream getOutStream() {
        return this.out;
    }

    public InputStream getInStream() {
        return this.is;
    }

    public String getFullIp(){
        return this.fullip;
    }

    public File getCacheFile(){
        return this.TempFile;
    }

    public Socket getSocket(){
        return this.sck;
    }

    public ArrayList<Byte> getBody(){
        return this.body;
    }

    public ArrayList<String> getAccepts(){
        return this.accepts;
    }

    public boolean isMIMEAcceptable(String MIME){
        if (this.accepts.contains("*/*"))
            return true;
        else{
            if (this.accepts.contains(MIME.split("/")[0] + "/*"))
                return true;
            else
                return this.accepts.contains(MIME);
        }
    }

    public String getProt(){
        return this.Prot;
    }

    public int getResponseCode(){
        return this.responseCode;
    }

    public boolean shouldBeCompressed(){
        return this.compression;
    }

    public boolean getKeepAlive(){
        return this.keepAlive;
    }

    public Algorithm getCompressionAlg(){
        return this.compressionAlg;
    }

    public byte[] getConvertedBody(){
        return this.convertedBody;
    }

    public void convertBody(){
        if (convertedBody == null){
            convertedBody = new byte[body.size()];
            int i = 0;
            for (Byte b : body)
                convertedBody[i++] = b;
            body.clear();
            body = null;
        }
    }

    public void clearRequest(){
        boolean bl = this.TempFile.delete();
        if (SocketsData.getInstance().maxReached(this.sck)){
            try{
                this.sck.close();
            }catch (Exception ex){
                Logger.logException(ex);
            }
        }
        this.Path = null;
        if (body != null){
            this.body.clear();
            this.body = null;
        }
        this.url = null;
        this.Host = null;
        this.Prot = null;
        this.headers.clear();
        this.headers = null;
        this.TempFile = null;
        this.out = null;
        this.is = null;
        this.ip = null;
        this.fullip = null;
        this.sck = null;
        this.id = null;
        System.gc();
    }
}
