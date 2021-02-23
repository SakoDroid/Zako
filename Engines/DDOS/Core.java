package Engines.DDOS;

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
                            obj.wait(20000);
                            requests.clear();
                        }
                        warnings.clear();
                    }
                }
            }catch(Exception ex){
                Logger.logException(ex);
            }
        }
    }

    private class HandleWarnings extends Thread {

        private final String ip;
        private final String host;

        public HandleWarnings (String ip,String hostName){
            this.host = hostName;
            this.ip = ip;
            this.start();
        }

        @Override
        public void run(){
            this.addWarning();
            this.checkWarning();
        }

        private void addWarning(){
            Logger.tlog("A warning has been added for " + ip,host);
            if (warnings.get(ip) != null){
                int wrn = warnings.get(ip);
                warnings.replace(ip, ++wrn);
            }else warnings.put(ip,1);
        }

        private void checkWarning(){
            if (warnings.get(ip) > 25){
                Logger.tlog(ip + " warning numbers passed 25. ip has been blocked.",host);
                Perms.addIPToBlackList(ip);
            }
        }
    }

    public void increaseReqVol(String ip,long size){
        long[] tmp = requests.get(ip);
        if (tmp != null) requests.get(ip)[1] += size;
    }

    public boolean trackIP(String ip,String hostName){
        boolean perm = true;
        if (requests.get(ip) != null){
            requests.get(ip)[0]++;
            if (requests.get(ip)[0] > 150){
                Logger.tlog("Possible DDOS or DOS attack detected from " + ip + ". Access has been disabled Temporarily . Tracking ip activity...",hostName);
                perm = false;
                new HandleWarnings(ip,hostName);
            }
            else if (requests.get(ip)[1] > 1500000000){
                Logger.tlog("Possible DDOS or DOS attack detected from " + ip + ". Access has been disabled Temporarily . Tracking ip activity...",hostName);
                perm = false;
                new HandleWarnings(ip,hostName);
            }
        }else{
            long[] temp = {1,0};
            requests.put(ip,temp);
        }
        return perm;
    }

    public void addWarning(String ip, String host){
        new HandleWarnings(ip,host);
    }

    public void clearRecord(String ip){
        requests.get(ip)[0] = 0;
        requests.get(ip)[1] = 0;
    }
}