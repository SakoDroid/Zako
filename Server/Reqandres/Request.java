package Server.Reqandres;

import Server.Utils.*;

import java.io.*;
import java.net.Socket;

public class Request {

    private final int id;
    private final String ip;
    private final String fullip;
    private String Host;
    private final Socket sck;
    private final File TempFile;
    public DataOutputStream out;
    public InputStream is;

    public Request (Socket client){
        this.id = basicUtils.getID();
        this.sck = client;
        this.ip = client.getInetAddress().getHostAddress();
        this.fullip = client.getRemoteSocketAddress().toString();
        this.TempFile = new File(Configs.getCWD() + "/Temp/temp" + id + ".tmp");
        try{
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
