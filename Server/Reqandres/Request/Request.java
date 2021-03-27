package Server.Reqandres.Request;

import Server.Utils.*;
import Server.Utils.Configs.Configs;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class Request {

    private String id;
    private String ip;
    private String fullip;
    private Socket sck;
    private File TempFile;
    private String Host;
    private String Prot = "HTTP/1.1";
    private URL url;
    private Methods method;
    private HashMap<String,String> headers = new HashMap<>();
    public DataOutputStream out;
    public InputStream is;
    private String Path;
    private String orgPath;
    private ArrayList<Byte> body = new ArrayList<>();
    private boolean keepAlive;

    public Request(Socket client){
        this.id = java.util.UUID.randomUUID().toString();
        this.sck = client;
        this.ip = client.getInetAddress().getHostAddress();
        this.fullip = client.getRemoteSocketAddress().toString();
        this.TempFile = new File(Configs.getCWD() + "/Temp/temp" + id + ".tmp");
        this.setHost("default");
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

    public void setProt(String prot){
        if (this.Prot == null){
            if (prot.equalsIgnoreCase("http/1.1"))
                this.Prot = "HTTP/1.1";
            else if (prot.equals("h2c") || prot.equals("h2"))
                this.Prot = "HTTP/2";
        }
    }

    public String getProt(){
        return this.Prot;
    }

    public void setHost(String host){
        this.Host = URLDecoder.decode(host,StandardCharsets.UTF_8);
        this.setTimeout(Configs.getTimeOut(this.Host));
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

    public String getFullip(){
        return this.fullip;
    }

    public File getCacheFile(){
        return this.TempFile;
    }

    public Socket getSocket(){
        return this.sck;
    }

    public boolean getKeepAlive(){
        return this.keepAlive;
    }

    public ArrayList<Byte> getBody(){
        return this.body;
    }

    public void clearRequest(){
        boolean bl = this.TempFile.delete();
        this.Path = null;
        this.body.clear();
        this.body = null;
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
