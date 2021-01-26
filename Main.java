import LoadBalancer.Reporter;
import Server.Utils.*;

public class Main extends Thread{

    private final Object lock = new Object();

    @Override
    public void run(){
        Logger.ilog("Server is starting ...");
        try {
            basicUtils.killPrcs();
            Logger.ilog("Loading requirements ...");
            Loader.load();
            if (Configs.autoUpdate)
                new ConfigsUpdater();
            new Reporter();
            if (Configs.isLBOn()){
                LoadBalancer.Configs.load();
                new LoadBalancerMainThread();
            }
            if (Configs.isWSOn()){
                if (SSLConfigs.SSL) new HttpsServerMainThread();
                else new HttpServerMainThread();
            }
            if (Loader.autoRs){
                synchronized (lock) {
                    lock.wait();
                }
            }
        } catch (Exception ex) {
            Logger.logException(ex);
        }
    }

    public static void main (String[] args) {
        Loader.loadRs();
        Thread mt = new Main();
        mt.start();
        if (Loader.autoRs) Runtime.getRuntime().addShutdownHook(new Main());
    }
}