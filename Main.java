import LoadBalancer.Reporter;
import Server.Utils.*;
import Server.Utils.Configs.Configs;
import Server.Utils.Configs.SSLConfigs;
import java.util.HashSet;

public class Main extends Thread{

    @Override
    public void run(){
        boolean sslSocketOpened = false;
        HashSet<Integer> toBeOpenedPorts = new HashSet<>();
        HashSet<Integer> OpenedPorts = new HashSet<>();
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
                if (LoadBalancer.Configs.on){
                    if (LoadBalancer.Configs.ssl.SSL){
                        new HttpsServerMainThread(LoadBalancer.Configs.port, LoadBalancer.Configs.host,true);
                        OpenedPorts.add(443);
                        OpenedPorts.add(80);
                        sslSocketOpened = true;
                    }
                    else {
                        new HttpServerMainThread(LoadBalancer.Configs.port,true);
                        OpenedPorts.add(LoadBalancer.Configs.port);
                    }
                }
                for (String host : Configs.getPorts().keySet()) {
                    if (host.equals("default") && Configs.getPorts().keySet().size() > 1)
                        continue;
                    int port = Configs.getPorts().get(host);
                    if (OpenedPorts.contains(port)){
                        Logger.ilog("Error! Port " + port + " has been already opened.");
                        System.out.println("Error! Port " + port + " has been already opened.");
                        continue;
                    }
                    if (SSLConfigs.isSSLOn(host)) {
                        if (!sslSocketOpened) {
                            sslSocketOpened = true;
                            new HttpsServerMainThread(port, host,false);
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
                            new HttpServerMainThread(port,false);
                    }
                    else if (port == 443){
                        Logger.ilog("Error! Port 443 cannot be opened for HTTP website.");
                        System.out.println("!** Error! Port 443 cannot be opened for HTTP website.");
                    }
                    else{
                        if (OpenedPorts.contains(port)){
                            Logger.ilog("Error! Port " + port + " has been already opened.");
                            System.out.println("Error! Port " + port + " has been already opened.");
                            continue;
                        }
                        new HttpServerMainThread(port,false);
                    }
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