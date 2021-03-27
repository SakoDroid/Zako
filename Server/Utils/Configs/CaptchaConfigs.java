package Server.Utils.Configs;

import Server.Utils.JSON.*;
import java.io.File;
import java.util.HashMap;

public class CaptchaConfigs {

    private static final HashMap<String,CPConfig> configs = new HashMap<>();

    public static void load(File fl){
        configs.put(fl.getName(),new CPConfig(fl));
    }

    public static boolean isON(String host){
        return configs.get(host).ON;
    }

    public static boolean isUCSOn(String host){
        return configs.get(host).UCS;
    }

    public static int getHardness(String host) {
        return configs.get(host).hardness;
    }

    public static int getLength(String host){
        return configs.get(host).length;
    }

    public static String getCGA(String host){
        return configs.get(host).CGA;
    }

    public static String getCPA(String host){
        return configs.get(host).CPA;
    }

    private static class CPConfig{

        public boolean ON;
        public boolean UCS;
        public int length;
        public int hardness;
        public String CGA;
        public String CPA;

        public CPConfig(File fl){
            this.load(fl);
        }

        private void load(File fl){
            JSONBuilder bld = JSONBuilder.newInstance();
            JSONDocument doc = bld.parse(new File(fl.getAbsolutePath() + "/Main.conf"));
            HashMap data = (HashMap) doc.toJava();
            HashMap cap = (HashMap) data.get("CAPTCHA");
            ON = (Boolean) cap.get("ON");
            length = Integer.parseInt(String.valueOf(cap.get("CAPTCHA length")));
            hardness =  Integer.parseInt(String.valueOf(cap.get("CAPTCHA hardness")));
            CGA = String.valueOf(cap.get("CGA"));
            CPA = String.valueOf(cap.get("CPA"));
            UCS = (Boolean) cap.get("UCS");
        }
    }
}
