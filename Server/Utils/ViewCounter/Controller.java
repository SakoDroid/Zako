package Server.Utils.ViewCounter;

import java.util.HashMap;
import java.util.TimerTask;

public class Controller {

    private final HashMap<String,ViewCore> cores = new HashMap<>();
    private static Controller cnt;

    private Controller(HashMap<String,HashMap<String,String>> hosts, long updateTime){
        for (String host : hosts.keySet())
            cores.put(host,
                    new ViewCore(hosts.get(host).get("Root"))
            );
    }

    public ViewCore getViewCore(String hostName){
        return this.cores.get(hostName);
    }

    public static Controller getInstance(){
        return cnt;
    }

    public static void load(HashMap<String,HashMap<String,String>> hosts, long updateTime){
        cnt = new Controller(hosts,updateTime);
    }
}
