package Server.Utils;

import java.io.*;
import java.util.HashMap;
import java.util.regex.*;

public class APIConfigs {

    private static final HashMap<String,APIConfig> configs = new HashMap<>();

    private APIConfigs(){}

    public static void load(File fl){
        configs.put(fl.getName(),new APIConfig(fl));
    }

    public static String[] getAPIAddress(String fullPath,String host){
        return configs.get(host).getAPIAddress(fullPath);
    }

    private static class APIConfig{

        private final HashMap<Pattern,String[]> apis = new HashMap<>();

        public APIConfig(File fl){
            this.load(fl);
        }

        private void load(File fl){
            try(BufferedReader bf = new BufferedReader(new FileReader(fl.getAbsolutePath() + "/API.cfg"))){
                String line;
                while((line = bf.readLine()) != null){
                    if (!line.startsWith("#")) addAPI(line);
                }
            }catch(Exception ex){
                Logger.logException(ex);
            }
        }

        private void addAPI(String line){
            String[] api = line.split("->");
            String path = api[1].trim();
            Pattern ptn = Pattern.compile(":\\d+");
            Matcher mc = ptn.matcher(path);
            if (mc.find())
                apis.put(Pattern.compile(api[0].trim())
                        ,cleanURL(path).split(":"));
            else{
                if (new File(path).exists() || path.startsWith("/"))
                    apis.put(Pattern.compile(api[0].trim())
                            ,new String[]{path});
                else
                    apis.put(Pattern.compile(api[0].trim())
                            ,new String[]{cleanURL(path),"80"});
            }
        }

        private String cleanURL(String uncleaned){
            return uncleaned.toLowerCase()
                    .replace("http://","")
                    .replace("https://","")
                    .trim();
        }

        public String[] getAPIAddress(String fullPath){
            for (Pattern ptn : apis.keySet()){
                Matcher mc = ptn.matcher(fullPath.trim());
                if (mc.find()){
                    return apis.get(ptn);
                }
            }
            return null;
        }
    }
}