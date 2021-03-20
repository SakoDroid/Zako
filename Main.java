import LoadBalancer.Reporter;
import Server.Utils.*;

public class Main extends Thread{

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
        } catch (Exception ex) {
            Logger.logException(ex);
        }
    }

    public static void main (String[] args) {
        new Main().start();
    }
}