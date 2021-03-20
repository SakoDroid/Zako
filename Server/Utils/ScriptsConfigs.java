package Server.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import Server.Utils.JSON.*;

public class ScriptsConfigs {

    private static final HashMap<String,SCConfig> configs = new HashMap<>();

    public static void load(File fl){
        configs.put(fl.getName(),new SCConfig(fl));
    }

    public static int getHandleMode(String ext,String host){
        return configs.get(host).getHandleMode(ext);
    }

    private static class SCConfig{

        private final HashMap<String,Integer> modes = new HashMap<>();

        public SCConfig(File fl){
            this.load(fl);
        }

        private void load(File fl){
            JSONBuilder bld = JSONBuilder.newInstance();
            JSONDocument doc = bld.parse(fl.getAbsolutePath() + "/Main.cfg");
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

        public int getHandleMode(String ext){
            return modes.get(ext);
        }
    }
}
