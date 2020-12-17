import Server.Utils.*;

public class Main{
    public static void main (String[] args) {
        Logger.ilog("Server is starting ...");
        try {
            Logger.ilog("Loading requirements ...");
            Loader.load();
            if (Configs.isLBOn()) new LoadBalancerMainThread();
            if (Configs.isWSOn()){
                if (Configs.isSSLOn()) new HttpsServerMainThread();
                else new HttpServerMainThread();
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
}