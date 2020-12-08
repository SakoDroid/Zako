package Server.Utils;

import java.io.*;
import java.util.HashMap;

public class APIConfigs {

    private static final HashMap<String,String[]> apis = new HashMap<>();

    private APIConfigs(){}

    private static void addAPI(String line){
        String[] api = line.split("->");
        apis.put(api[0].trim(),api[1].trim().split(":"));
    }

    public static void load(){
        try(BufferedReader bf = new BufferedReader(new FileReader(Configs.getCWD() + "/src/CFGS/API.cfg"))){
            String line;
            while((line = bf.readLine()) != null){
                if (!line.startsWith("#")) addAPI(line);
            }
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    public static String[] getAPIAddress(String fullPath){
        return apis.get(fullPath);
    }
}
