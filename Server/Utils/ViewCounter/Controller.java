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
        this.startWriteTimer(updateTime);
        this.startResetTimers();
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

    private void startWriteTimer(long updateTime){
        javax.swing.Timer writeTimer = new javax.swing.Timer((int)updateTime,e -> {
            new Writer(cores).writeAll();
        });
        java.util.Timer writeTtimer = new java.util.Timer(false);
        writeTtimer.schedule(new TimerTask() {
            @Override
            public void run() {
                writeTimer.start();
            }
        },0);
    }

    private void startResetTimers(){
        javax.swing.Timer dayTimer = new javax.swing.Timer(24 * 3600 * 1000, e -> {
            for (ViewCore vc : cores.values())
                vc.reset24hViews();
        });
        java.util.Timer dayTtimer = new java.util.Timer(false);
        dayTtimer.schedule(new TimerTask() {
            @Override
            public void run() {
                dayTimer.start();
            }
        },0);

        javax.swing.Timer weekTimer = new javax.swing.Timer(7 * 24 * 3600 * 1000, e -> {
            for (ViewCore vc : cores.values())
                vc.resetWeekViews();
        });
        java.util.Timer weekTtimer = new java.util.Timer(false);
        weekTtimer.schedule(new TimerTask() {
            @Override
            public void run() {
                weekTimer.start();
            }
        },0);
    }
}
