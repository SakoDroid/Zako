package Server.Utils;

import java.io.*;
import java.util.HashMap;
import java.util.regex.*;

public class APIConfigs {

    private static final HashMap<Pattern,String[]> apis = new HashMap<>();

    private APIConfigs(){}

    private static void addAPI(String line){
        String[] api = line.split("->");
        String path = api[1].trim();
        Pattern ptn = Pattern.compile(":\\d+");
        Matcher mc = ptn.matcher(path);
        if (mc.find())
            apis.put(Pattern.compile(api[0].trim())
                    ,cleanURL(path).split(":"));
        else{
            File fl = new File(path);
            if (fl.exists())
                apis.put(Pattern.compile(api[0].trim())
                        ,new String[]{path});
            else
                apis.put(Pattern.compile(api[0].trim())
                        ,new String[]{cleanURL(path),"80"});
        }
    }

    private static String cleanURL(String uncleaned){
        return uncleaned.toLowerCase()
                .replace("http://","")
                .replace("https://","")
                .trim();
    }

    public static void load(){
        File fl = null;
        if (System.getProperty("os.name")
                .toLowerCase().contains("windows"))
            fl = new File(System.getProperty("user.dir") + "/Configs/API.cfg");
        else if (System.getProperty("os.name")
                .toLowerCase().contains("linux"))
            fl = new File("/etc/zako-web/API.cfg");
        try(BufferedReader bf = new BufferedReader(new FileReader(fl))){
            String line;
            while((line = bf.readLine()) != null){
                if (!line.startsWith("#")) addAPI(line);
            }
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    public static String[] getAPIAddress(String fullPath){
        for (Pattern ptn : apis.keySet()){
            Matcher mc = ptn.matcher(fullPath.trim());
            if (mc.find()){
                return apis.get(ptn);
            }
        }
        return null;
    }
}