package Server.Utils.ViewCounter;

import Server.Utils.Configs;
import Server.Utils.JSON.JSONBuilder;
import java.io.File;
import java.util.HashMap;

public class Writer {

    private final HashMap<String,ViewCore> writableCores;

    public Writer(HashMap<String,ViewCore> cores){
        this.writableCores = cores;
    }

    public void writeAll(){
        for (String host : writableCores.keySet()){
            String mainDir = Configs.getMainDir(host);
            if (mainDir == null)
                mainDir = Configs.getLogsDir(host);
            File views  = new File(mainDir + "/views.json");
            if (!views.isFile()){
                try{
                    views.createNewFile();
                }catch (Exception ignored){}
            }
            JSONBuilder.newInstance()
                    .write(writableCores.get(host).toJson(),views);
        }
    }

}
