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
        File fl = null;
        if (System.getProperty("os.name")
                .toLowerCase().contains("windows"))
            fl = new File(System.getProperty("user.dir") + "/Configs/Zako.cfg");
        else if (System.getProperty("os.name")
                .toLowerCase().contains("linux"))
            fl = new File("/etc/zako-web/Zako.cfg");
        HashMap data = (HashMap) JSONBuilder.newInstance()
                .parse(fl)
                .toJava();
        long time = (Long) data.get("CFG Update period");
        while (true){
            Logger.ilog("Configurations will be updated in " + time + " milli seconds.");
            synchronized (lock){
                try{
                    lock.wait(time);
                }catch (Exception ignored){}
            }
            Logger.ilog("Updating configurations ...");
            Loader.load();
            Logger.ilog("Update done!");
        }
    }
}
