import LoadBalancer.Reporter;
import Server.Utils.*;

import java.util.HashSet;

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
            HashSet<Integer> openedHttpPorts = new HashSet<>();
            boolean sslSocketOpened = false;
            for (String host : Configs.getPorts().keySet()){
                int port = Configs.getPorts().get(host);
                if (!openedHttpPorts.contains(port)){
                    openedHttpPorts.add(port);
                    if (SSLConfigs.isSSLOn(host)){
                        if (!sslSocketOpened){
                            sslSocketOpened = true;
                            new HttpsServerMainThread(port,host);
                        }else
                            Logger.ilog("Error! Cant serve more than two https enabled web sites on one port.");
                    }else
                        new HttpServerMainThread(port);
                }
            }
        } catch (Exception ex) {
            Logger.logException(ex);
        }
    }

    public static void main (String[] args) {
        new Main().start();
    }
}