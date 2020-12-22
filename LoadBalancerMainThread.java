import LoadBalancer.Forwarder;
import Server.Utils.Configs;
import Server.Utils.Logger;
import java.net.*;

public class LoadBalancerMainThread extends Thread{

    public LoadBalancerMainThread(){
        this.start();
    }

    @Override
    public void run(){
        try{
            ServerSocket ss = new ServerSocket(Configs.getLBPort());
            Logger.ilog("Load balancer thread is now running on port " + Configs.getLBPort() + " ...");
            while(true) new Forwarder(ss.accept());
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
