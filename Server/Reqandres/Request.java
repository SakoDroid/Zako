package Server.Reqandres;

import Server.Utils.*;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;

public class Request {

    private final int id;
    private final String ip;
    private final String fullip;
    private final Socket sck;
    private final File TempFile;
    private String Host;
    private String Prot;
    private URL url;
    private Methods method;
    private HashMap headers = new HashMap();
    public DataOutputStream out;
    public InputStream is;
    public String Path;
    public String orgPath;

    public Request (Socket client){
        this.id = basicUtils.getID();
        this.sck = client;
        this.ip = client.getInetAddress().getHostAddress();
        this.fullip = client.getRemoteSocketAddress().toString();
        this.TempFile = new File(Configs.getCWD() + "/Temp/temp" + id + ".tmp");
        try{
            this.sck.setSoTimeout(Configs.timeout);
            this.is = client.getInputStream();
            this.out = new DataOutputStream(client.getOutputStream());
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    public void setHeaders(HashMap hd){
        this.headers = hd;
    }

    public HashMap getHeaders(){
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

    public void setProt(String prot){
        this.Prot = prot;
    }

    public String getProt(){
        return this.Prot;
    }

    public void setHost(String host){
        this.Host = host;
    }

    public String getHost(){
        return this.Host;
    }

    public int getID(){
        return this.id;
    }

    public String getIP(){
        return this.ip;
    }

    public String getFullip(){
        return this.fullip;
    }

    public File getCacheFile(){
        return this.TempFile;
    }

    public Socket getSock(){
        return this.sck;
    }
}
