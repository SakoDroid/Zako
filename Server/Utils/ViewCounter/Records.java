package Server.Utils.ViewCounter;

import Server.Utils.Logger;
import java.util.HashSet;

public class Records {

    private final HashSet<String> records = new HashSet<>();
    private static final Records rcd = new Records();

    private Records(){}

    public void addRecord(String ip){
        records.add(ip);
        new RecDeleter(ip);
    }

    public boolean isValid(String ip){
        return !records.contains(ip);
    }

    public static Records getInstance(){
        return rcd;
    }

    private class RecDeleter extends Thread{

        private final String ip;
        private final Object lock = new Object();

        public RecDeleter(String IP){
            this.ip = IP;
            this.start();
        }

        @Override
        public void run(){
            synchronized (lock){
                try{
                    lock.wait(5000);
                    records.remove(ip);
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
    }
}
