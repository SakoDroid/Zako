import Server.HttpListener;
import Server.Utils.Configs;
import Server.Utils.Logger;
import java.net.ServerSocket;

public class HttpServerMainThread extends Thread{

    public HttpServerMainThread(){
        this.start();
    }

    @Override
    public void run(){
        try{
            ServerSocket server = new ServerSocket(Configs.getWSPort());
            Logger.ilog("Http server thread is now running on port " + Configs.getWSPort() + " ...");
            while(true) new HttpListener(server.accept());
        }catch (Exception ex) {
            Logger.logException(ex);
        }

        Logger.ilog("Server is shutting down ...");
    }
}
