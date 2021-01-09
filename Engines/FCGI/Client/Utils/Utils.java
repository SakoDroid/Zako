package Engines.FCGI.Client.Utils;

import java.util.*;

public class Utils {

    private static final Random rnd = new Random();
    private static final List<Integer> ids = new ArrayList<>();
    private static HashMap<String,String> envs;

    public static int getID(){
        int tempId;
        do {
            tempId = rnd.nextInt(65535);
        }while (ids.contains(tempId));
        ids.add(tempId);
        return tempId;
    }

    public static void setEnvs(HashMap<String,String> data){
        envs = data;
    }

    public static void delId(int id){
        ids.remove(id);
    }

    public static void fixEnvs(Map<String,String> en){
        for (String key : envs.keySet()){
            String value = envs.get(key).trim();
            if (value.startsWith("$"))
                en.put(key,en.get(value.replace("$","")));
            else
                en.put(key,value);
        }
    }
}
