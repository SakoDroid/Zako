package Server.DDOS;

import Server.Utils.Logger;
import Server.Utils.Perms;
import java.util.HashMap;

class Core {

    private final HashMap<String,long[]> requests = new HashMap<>();
    private final HashMap<String,Integer> warnings = new HashMap<>();

    public Core (){
        new Deleter();
    }

    private class Deleter extends Thread{

        private final Object obj = new Object();

        public Deleter(){
            this.start();
        }

        @Override
        public void run(){
            try{
                synchronized (obj){
                    while (true) {
                        for (int i = 0; i < 200; i++) {
                            obj.wait(15000);
                            requests.clear();
                        }
                        warnings.clear();
                    }
                }
            }catch(Exception ex){
                String t = "";
                for (StackTraceElement a : ex.getStackTrace()) {
                    t += a.toString() + " ;; ";
                }
                t += ex.toString();
                Logger.ilog(t);
            }
        }
    }

    private class HandleWarnings extends Thread {

        private String ip;

        public HandleWarnings (String ip){
            this.ip = ip;
            this.start();
        }

        @Override
        public void run(){
            this.addWarning();
            this.checkWarning();
        }

        private void addWarning(){
            Logger.tlog("A warning has been added for " + ip);
            if (warnings.get(ip) != null){
                int wrn = warnings.get(ip);
                warnings.replace(ip, ++wrn);
            }else warnings.put(ip,1);
        }

        private void checkWarning(){
            if (warnings.get(ip) > 25){
                Logger.tlog(ip + " warning numbers passed 50. ip has been blocked.");
                Perms.addIPToBlackList(ip);
            }
        }
    }

    public void increaseReqVol(String ip,long size){
        requests.get(ip)[1] += size;
    }

    public boolean trackIP(String ip){
        boolean perm = true;
        if (requests.get(ip) != null){
            requests.get(ip)[0]++;
            if (requests.get(ip)[0] > 150){
                Logger.tlog("Possible DDOS or DOS attack detected from " + ip + ". Access has been disabled Temporarily . Tracking ip activity...");
                perm = false;
                new HandleWarnings(ip);
            }
            else if (requests.get(ip)[1] > 1500000000){
                Logger.tlog("Possible DDOS or DOS attack detected from " + ip + ". Access has been disabled Temporarily . Tracking ip activity...");
                perm = false;
                new HandleWarnings(ip);
            }
        }else{
            long[] temp = {1,0};
            requests.put(ip,temp);
        }
        return perm;
    }
}