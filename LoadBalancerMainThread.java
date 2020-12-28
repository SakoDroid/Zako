import LoadBalancer.Forwarder;
import Server.Utils.Configs;
import Server.Utils.Logger;
import java.net.*;
import java.nio.channels.ServerSocketChannel;

public class LoadBalancerMainThread extends Thread{

    public LoadBalancerMainThread(){
        this.start();
    }

    @Override
    public void run(){
        try{
            ServerSocket gate = new ServerSocket(Configs.getLBPort());
            Logger.ilog("Load balancer thread is now running on port " + Configs.getLBPort() + " ...");
            while(true) new Forwarder(gate.accept());
        }catch (Exception ex) {
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }

        Logger.ilog("Load balancer is shutting down ...");
    }

}
