package Server.Utils;

import java.io.*;
import java.util.HashMap;
import java.util.regex.*;

public class APIConfigs {

    private static final HashMap<String,String[]> apis = new HashMap<>();

    private APIConfigs(){}

    private static void addAPI(String line){
        String[] api = line.split("->");
        String path = api[1].trim();
        Pattern ptn = Pattern.compile(":\\d+");
        Matcher mc = ptn.matcher(path);
        if (mc.find())
            apis.put(api[0].trim(),cleanURL(path).split(":"));
        else{
            File fl = new File(path);
            if (fl.exists())
                apis.put(api[0].trim(),new String[]{path});
            else
                apis.put(api[0].trim(),new String[]{cleanURL(path),"80"});
        }
    }

    private static String cleanURL(String uncleaned){
        return uncleaned.toLowerCase()
                .replace("http://","")
                .replace("https://","")
                .trim();
    }

    public static void load(){
        try(BufferedReader bf = new BufferedReader(new FileReader("/etc/zako-web/API.cfg"))){
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
        for (String api : apis.keySet()){
            Pattern ptn = Pattern.compile(api.trim());
            Matcher mc = ptn.matcher(fullPath.trim());
            if (mc.find()){
                return apis.get(api);
            }
        }
        return null;
    }
}