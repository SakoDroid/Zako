import Server.HttpListener;
import Server.Utils.Logger;
import java.net.ServerSocket;

public class HttpServerMainThread extends Thread{

    private final int port;
    public final boolean lb;

    public HttpServerMainThread(int port,boolean lb){
        this.lb = lb;
        this.port = port;
        this.start();
    }

    @Override
    public void run(){
        try{
            ServerSocket server = new ServerSocket(port);
            Logger.ilog("Http server thread is now running on port " + port + " ...");
            System.out.println("Http server thread is now running on port " + port + " ...");
            while(true) new HttpListener(server.accept(),"default",lb);
        }catch (Exception ex) {
            Logger.logException(ex);
        }
        Logger.ilog("Server is shutting down ...");
        System.out.println("Server is shutting down ...");
    }
}
