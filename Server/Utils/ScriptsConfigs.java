package Server.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import Server.Utils.JSON.*;

public class ScriptsConfigs {

    private static final HashMap<String,Integer> modes = new HashMap<>();

    public static void load(){
        File fl = null;
        if (System.getProperty("os.name")
                .toLowerCase().contains("windows"))
            fl = new File(System.getProperty("user.dir") + "/Configs/Zako.cfg");
        else if (System.getProperty("os.name")
                .toLowerCase().contains("linux"))
            fl = new File("/etc/zako-web/Zako.cfg");
        JSONBuilder bld = JSONBuilder.newInstance();
        JSONDocument doc = bld.parse(fl);
        HashMap data = (HashMap) doc.toJava();
        HashMap scripts = (HashMap) data.get("Scripts");
        for (Object obj : scripts.keySet()){
            String ext = (String) obj;
            HashMap extData = (HashMap) scripts.get(obj);
            String mode = (String) extData.get("handle mode");
            if (mode.equals("CGI")) modes.put(ext,0);
            else {
                modes.put(ext,1);
                Engines.FCGI.Client.Utils.Configs.addServer(ext, (Map) extData.get("FCGI"));
            }
        }
        HashMap fcgi = (HashMap) data.get("FCGI");
        Engines.FCGI.Client.Utils.Utils.setEnvs((HashMap<String, String>) fcgi.get("Params"));
    }

    public static int getHandleMode(String ext){
        return modes.get(ext);
    }
}
