package Engines.Captcha;

import java.util.HashMap;
import java.util.TimerTask;

public class RecordDeleter {

    public void start(HashMap<String,String> records, String ip){
        javax.swing.Timer timer = new javax.swing.Timer(5 * 60 * 1000,e -> records.remove(ip));
        java.util.Timer tTimer = new java.util.Timer(false);
        tTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                timer.start();
            }
        },0);
    }
}
