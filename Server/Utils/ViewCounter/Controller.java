package Server.Utils.ViewCounter;

import Server.Utils.Logger;

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
        this.startWriters(updateTime);
    }

    public ViewCore getViewCore(String hostName){
        return this.cores.get(hostName);
    }

    public static Controller getInstance(){
        return cnt;
    }

    public static void load(HashMap<String,HashMap<String,String>> hosts, long updateTime){
        cnt = new Controller(hosts,updateTime);
        Logger.ilog("View counter is active ...");
    }

    private void startWriters(long updateTime){
        javax.swing.Timer writeTimer = new javax.swing.Timer((int)updateTime,e -> {
            new Writer(this.cores).writeAll();
        });
        java.util.Timer writeTtimer = new java.util.Timer(false);
        writeTtimer.schedule(new TimerTask() {
            @Override
            public void run() {
                writeTimer.start();
            }
        },0);
    }
}
