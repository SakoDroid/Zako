import Server.HttpHandler;
import Server.Utils.*;
import javax.net.ssl.*;

public class HttpsServerMainThread extends Thread{

    public HttpsServerMainThread(){
        this.start();
    }

    @Override
    public void run(){
        try{
            System.setProperty("javax.net.ssl.keyStore","/mnt/E8A8DC6AA8DC3930/Projects/tmrin/nj.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "shit123");
            SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketfactory.createServerSocket(Configs.getWSPort());
            Logger.ilog("Https server thread is now running ...");
            while (true) new HttpHandler(sslServerSocket.accept());
        }catch (Exception ex) {
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }
}
