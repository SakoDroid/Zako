package Server.Utils;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

public class Perms {

    private static final HashMap<String,PConfig> configs = new HashMap<>();
    private final static HashSet<String> ipBlackList = new HashSet<>();

    public static void load(File fl){
        configs.put(fl.getName(),new PConfig(fl));
    }

    public static void loadBlackList(){
        try (BufferedReader bf = new BufferedReader(new FileReader(Configs.baseAddress + "/IP-Blacklist"))){
            String line;
            while((line = bf.readLine()) != null)
                if (!line.startsWith("#") && !line.isEmpty())
                    ipBlackList.add(line.trim());
        }catch (Exception ex){
            Logger.logException(ex);
        }
    }

    public static boolean isIPAllowedForPUTAndDelete(String ip,String host){
        return configs.get(host).isIPAllowedForPUTAndDelete(ip);
    }

    public static boolean isIPAllowed(String ip){
        return !ipBlackList.contains(ip);
    }

    public static synchronized void addIPToBlackList(String ip){
        ipBlackList.add(ip);
        try(FileWriter fw = new FileWriter(Configs.baseAddress + "/IP-Blacklist",true)){
            fw.write("\n" + ip);
            fw.flush();
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    private static class PConfig{

        private final static HashSet<String> ipsAuthorizedForPUTAndDelete = new HashSet<>();
        private final File fl;

        public PConfig(File fl){
            this.fl = fl;
            this.load();
        }

        private void load(){
            try (BufferedReader bf = new BufferedReader(new FileReader(this.fl.getAbsolutePath() + "/sec/ILPD"))){
                String line;
                while((line = bf.readLine()) != null){
                    if (!line.startsWith("#") && !line.isEmpty()) ipsAuthorizedForPUTAndDelete.add(line);
                }
            }catch(Exception ex){
                Logger.logException(ex);
            }
        }

        public boolean isIPAllowedForPUTAndDelete(String ip){
            return ipsAuthorizedForPUTAndDelete.contains(ip);
        }
    }
}