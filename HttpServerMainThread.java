import Server.HttpHandler;
import Server.Utils.Configs;
import Server.Utils.Logger;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;

public class HttpServerMainThread extends Thread{

    public HttpServerMainThread(){
        this.start();
    }

    @Override
    public void run(){
        try{
            ServerSocketChannel serverSocket = ServerSocketChannel.open().bind(new InetSocketAddress(Configs.getWSPort()));
            Logger.ilog("Http server thread is now running on port " + Configs.getWSPort() + " ...");
            while(true) new HttpHandler(serverSocket.accept());
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
