import Server.HttpHandler;
import Server.Utils.*;
import javax.net.ssl.*;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpsServerMainThread{

    public HttpsServerMainThread(){
        new HTTPS();
        new HTTP();
    }

    private class HTTP extends Thread{

        public HTTP(){
            this.start();
        }

        private class Redirect extends Thread{

            private final Socket sock;

            public Redirect(Socket s){
                this.sock = s;
                this.start();
            }

            @Override
            public void run(){
                try{
                    DataOutputStream out = new DataOutputStream(sock.getOutputStream());
                    out.writeBytes("HTTP/1.1 301 Moved Permanently\nServer: " + basicUtils.Zako + "\nLocation: https://" + Configs.MainHostWithPort + "/\nConnection: close\n\n");
                    out.flush();
                    out.close();
                }catch(Exception ex){
                    String t = "";
                    for (StackTraceElement a : ex.getStackTrace()) {
                        t += a.toString() + " ;; ";
                    }
                    t += ex.toString();
                    Logger.ilog(t);
                }
            }

        }

        @Override
        public void run(){
            try{
                ServerSocket ss = new ServerSocket(80);
                Logger.ilog("Http thread is running on port 80 for redirection ...");
                while (true) new Redirect(ss.accept());
            }catch (Exception ex) {
                String t = "";
                for (StackTraceElement a : ex.getStackTrace()) {
                    t += a.toString() + " ;; ";
                }
                t += ex.toString();
                Logger.ilog(t);
            }
        }
    }

    private class HTTPS extends Thread{

        public HTTPS(){
            this.start();
        }

        @Override
        public void run () {
            try {
                System.setProperty("javax.net.ssl.keyStore", SSLConfigs.getJKS());
                System.setProperty("javax.net.ssl.keyStorePassword", SSLConfigs.getPass());
                SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
                SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketfactory.createServerSocket(Configs.getWSPort());
                Logger.ilog("Https server thread is now running (jks : " + SSLConfigs.getJKS() + " ,, jks pass : " + SSLConfigs.getPass() + ") ...");
                while (true) new HttpHandler((SSLSocket) sslServerSocket.accept());
            } catch (Exception ex) {
                String t = "";
                for (StackTraceElement a : ex.getStackTrace()) {
                    t += a.toString() + " ;; ";
                }
                t += ex.toString();
                Logger.ilog(t);
            }
            Logger.ilog("Server is shutting down ...");
        }
    }
}
