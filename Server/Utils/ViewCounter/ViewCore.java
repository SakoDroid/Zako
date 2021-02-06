package Server.Utils.ViewCounter;

import Server.Utils.JSON.*;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewCore {

    private long views = 0;
    private long views24 = 0;
    private long viewsWeek = 0;
    private long viewsMonth = 0;
    private long dayResetTime;
    private long weekResetTime;
    private long monthResetTime;

    public ViewCore(String rootAddress){
        this.load(rootAddress);
    }

    public synchronized void addView(String ip){
        views ++;
        views24 ++;
        viewsWeek ++;
        viewsMonth ++;
    }

    private void load(String add){
        File vw = new File(add + "/views.json");
        long currentTime = new Date().getTime();
        if (vw.isFile()){
            JSONBuilder builder = JSONBuilder.newInstance();
            HashMap data = (HashMap) builder.parse(vw).toJava();
            views = (Long) data.get("All views");
            views24 = (Long) data.get("Last day views");
            viewsWeek = (Long) data.get("Last week views");
            viewsMonth = (Long) data.get("Last month views");
            HashMap cfg = (HashMap) data.get("Configs");
            dayResetTime = (Long) cfg.get("24");
            weekResetTime = (Long) cfg.get("7");
            monthResetTime = (Long) cfg.get("30");
        }else{
            dayResetTime = currentTime + (24 * 3600 * 1000);
            weekResetTime = currentTime + (7 * 24 * 3600 * 1000);
            monthResetTime = currentTime + (30L * 7 * 24 * 3600 * 1000);
        }
        startTimers(currentTime);
    }

    public JSONDocument toJson(){
        HashMap data = new HashMap();
        HashMap cfgs = new HashMap();
        cfgs.put("24",dayResetTime);
        cfgs.put("7",weekResetTime);
        cfgs.put("30",monthResetTime);
        data.put("All views",views);
        data.put("Last day views",views24);
        data.put("Last week views",viewsWeek);
        data.put("Last month views",viewsMonth);
        data.put("Configs",cfgs);
        return new JSONDocument(data);
    }

    @Override
    public String toString(){
        return "All views : " + views +
                "\nviews24 : " + views24 +
                "\nviews week : " + viewsWeek +
                "\nview month : " + viewsMonth +
                "\n--------------------------------------";
    }

    private void startTimers(long ct){
        startDayTimer(dayResetTime,ct);
        startWeekTimer(weekResetTime,ct);
        startMonthTimer(monthResetTime,ct);
    }

    private void startDayTimer(long time,long currentTime){
        if (currentTime >= time){
            views24 = 0;
            time = currentTime;
        }
        javax.swing.Timer writeTimer = new javax.swing.Timer(24 * 3600 * 1000,e -> {
            dayResetTime = new Date().getTime() + (24 * 3600 * 1000);
            views24 = 0;
        });
        java.util.Timer writeTtimer = new java.util.Timer(false);
        writeTtimer.schedule(new TimerTask() {
            @Override
            public void run() {
                views24 = 0;
                writeTimer.start();
            }
        },time - currentTime);
    }

    private void startWeekTimer(long time,long currentTime){
        if (currentTime >= time){
            viewsWeek = 0;
            time = currentTime;
        }
        javax.swing.Timer writeTimer = new javax.swing.Timer(7 * 24 * 3600 * 1000,e -> {
            weekResetTime = new Date().getTime() + (7 * 24 * 3600 * 1000);
            viewsWeek = 0;
        });
        java.util.Timer writeTtimer = new java.util.Timer(false);
        writeTtimer.schedule(new TimerTask() {
            @Override
            public void run() {
                viewsWeek = 0;
                writeTimer.start();
            }
        },time - currentTime);
    }

    private void startMonthTimer(long time,long currentTime){
        AtomicInteger day = new AtomicInteger(1);
        if (currentTime >= time){
            viewsMonth = 0;
            time = currentTime;
        }
        javax.swing.Timer writeTimer = new javax.swing.Timer(7 * 24 * 3600 * 1000,e -> {
            if (day.get() == 30){
                viewsMonth = 0;
                day.set(0);
                monthResetTime = new Date().getTime() + (30L * 7 * 24 * 3600 * 1000);
            }
            else
                day.getAndIncrement();
        });
        java.util.Timer writeTtimer = new java.util.Timer(false);
        writeTtimer.schedule(new TimerTask() {
            @Override
            public void run() {
                writeTimer.start();
            }
        },time - currentTime);
    }
}
