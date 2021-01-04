package Server.Utils;

import java.util.HashMap;

public class ScriptsConfigs {

    private static final HashMap<String,Integer> modes = new HashMap<>();

    public static void load(){

    }

    public static int getHandleMode(String ext){
        return modes.get(ext);
    }
}
