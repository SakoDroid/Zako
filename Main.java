import LoadBalancer.Reporter;
import Server.Utils.*;

public class Main extends Thread{

    private final Object lock = new Object();

    @Override
    public void run(){
        Logger.ilog("Server is starting ...");
        try {
            Logger.ilog("Loading requirements ...");
            Loader.load();
            new Reporter();
            if (Configs.isLBOn()) new LoadBalancerMainThread();
            if (Configs.isWSOn()){
                if (Configs.isSSLOn()) new HttpsServerMainThread();
                else new HttpServerMainThread();
            }
            synchronized (lock){
                lock.wait();
            }
        } catch (Exception ex) {
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    public static void main (String[] args) {
        Loader.load();
        Logger.ilog("Server is starting ...");
        Thread mt = new Main();
        mt.start();
        if (Configs.autoRs) Runtime.getRuntime().addShutdownHook(new Main());
    }
}