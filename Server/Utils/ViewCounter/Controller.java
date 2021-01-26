package Server.Utils.ViewCounter;

import Server.Utils.Logger;

import java.util.HashMap;

public class Controller {

    private final HashMap<String,ViewCore> cores = new HashMap<>();
    private static Controller cnt;

    private Controller(HashMap<String,HashMap<String,String>> hosts, long updateTime){
        for (String host : hosts.keySet())
            cores.put(host,
                    new ViewCore(hosts.get(host).get("Root"))
            );
        new WriteThread(updateTime);
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

    private class WriteThread extends Thread{

        private final long ut;
        private final Object lock = new Object();

        public WriteThread(long updateTime){
            this.ut = updateTime;
            this.start();
        }

        @Override
        public void run(){
            synchronized (lock){
                try{
                    while (true){
                        lock.wait(ut);
                        new Writer(cores).writeAll();
                    }
                }catch(Exception ex){
                    Logger.logException(ex);
                }
            }
        }
    }
}
