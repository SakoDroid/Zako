import Server.Utils.JSON.JSONBuilder;
import Server.Utils.Logger;
import java.io.File;
import java.util.HashMap;

public class ConfigsUpdater extends Thread{

    private final Object lock = new Object();

    public ConfigsUpdater(){
        this.start();
    }

    @Override
    public void run(){
        HashMap data = (HashMap) JSONBuilder.newInstance()
                .parse(new File("/etc/zako/Zako.cfg"))
                .toJava();
        long time = (Long) data.get("CFG Update period");
        while (true){
            synchronized (lock){
                try{
                    lock.wait(time);
                }catch (Exception ignored){}
            }
            Logger.ilog("Updating configurations ...");
            Loader.load();
            Logger.ilog("Update done!");
            Logger.ilog("Configurations will be updated in " + time + " milli seconds.");
        }
    }
}