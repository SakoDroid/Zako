package Server.Reqandres;

import Server.Utils.*;

import java.io.DataOutputStream;
import java.io.*;
import java.nio.channels.SocketChannel;

public class Request {

    private final int id;
    private final String ip;
    private final String fullip;
    private String Host;
    private final SocketChannel sh;
    private final File TempFile;
    private DataOutputStream out;

    public Request (SocketChannel client){
        this.id = basicUtils.getID();
        this.sh = client;
        this.ip = client.socket().getInetAddress().getHostAddress();
        this.fullip = client.socket().getRemoteSocketAddress().toString();
        this.TempFile = new File(Configs.getCWD() + "/Temp/temp" + id + ".tmp");
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

    public void setBlockingMode(boolean mode){
        try{
            this.sh.configureBlocking(mode);
        }catch(IOException ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    public void genOutputStream(){
        try{
            if (out == null) out = new DataOutputStream(this.sh.socket().getOutputStream());
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    public DataOutputStream getOutputStream(){
        return this.out;
    }
}
