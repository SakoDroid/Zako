import LoadBalancer.Reporter;
import Server.Utils.*;
import Server.Utils.Configs.Configs;
import Server.Utils.Configs.SSLConfigs;

import java.util.HashSet;

public class Main extends Thread{

    @Override
    public void run(){
        Logger.ilog("Server is starting ...");
        System.out.println("Server is starting ...");
        Logger.ilog("Running test ...");
        System.out.println("Running test ...");
        if (this.runTest()){
            Logger.ilog("Test OK! ...");
            System.out.println("Test OK! ...");
            try {
                basicUtils.killPrcs();
                Logger.ilog("Loading requirements ...");
                Loader.load();
                if (Configs.autoUpdate)
                    new ConfigsUpdater();
                new Reporter();
                if (Configs.isLBOn()) {
                    LoadBalancer.Configs.load();
                    new LoadBalancerMainThread();
                }
                HashSet<Integer> toBeOpenedPorts = new HashSet<>();
                boolean sslSocketOpened = false;
                for (String host : Configs.getPorts().keySet()) {
                    if (host.equals("default") && Configs.getPorts().keySet().size() > 1)
                        continue;
                    int port = Configs.getPorts().get(host);
                    if (SSLConfigs.isSSLOn(host)) {
                        if (!sslSocketOpened) {
                            sslSocketOpened = true;
                            new HttpsServerMainThread(port, host);
                        } else
                            Logger.ilog("Error! Cant serve more than one https enabled web site on one webserver.");
                    }else
                        toBeOpenedPorts.add(port);
                }
                for (int port : toBeOpenedPorts){
                    if (port == 80){
                        if (sslSocketOpened){
                            Logger.ilog("Error! SSL for one of the sites is enabled. Port 80 has been opened for redirection., can't run an http website on port 80.");
                            System.out.println("!** Error! SSL for one of the sites is enabled. Port 80 has been opened for redirection., can't run an http website on port 80.");
                        }
                        else
                            new HttpServerMainThread(port);
                    }
                    else if (port == 443){
                        Logger.ilog("Error! Port 443 cannot be opened for HTTP website.");
                        System.out.println("!** Error! Port 443 cannot be opened for HTTP website.");
                    }
                    else
                        new HttpServerMainThread(port);
                }
            } catch (Exception ex) {
                Logger.logException(ex);
            }
        }else {
            Logger.ilog("Test failed. Run \"zako test\" in terminal to see what's wrong.");
            System.out.println("Test failed. Run \"zako test\" in terminal to see what's wrong.");
        }
    }

    private boolean runTest(){
        /*try{
            ProcessBuilder pb = new ProcessBuilder("zako","test","-a");
            Process p = pb.start();
            String res = new String(p.getInputStream().readAllBytes());
            return res.trim().equals("true");
        }catch (Exception ex){
            Logger.logException(ex);
            return false;
        }*/
        return true;
    }

    public static void main (String[] args) {
        new Main().start();
    }
}