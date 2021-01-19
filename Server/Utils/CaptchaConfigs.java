package Server.Utils;

import Server.Utils.JSON.*;
import java.io.File;
import java.util.HashMap;

public class CaptchaConfigs {

    public static boolean ON;
    public static int length;
    public static int hardness;
    public static String CGA;
    public static String CPA;

    public static void load(){
        JSONBuilder bld = JSONBuilder.newInstance();
        JSONDocument doc = bld.parse(new File("/etc/zako/Zako.cfg"));
        HashMap data = (HashMap) doc.toJava();
        HashMap cap = (HashMap) data.get("CAPTCHA");
        ON = (Boolean) cap.get("ON");
        length = Integer.parseInt(String.valueOf(cap.get("CAPTCHA length")));
        hardness =  Integer.parseInt(String.valueOf(cap.get("CAPTCHA hardness")));
        CGA = String.valueOf(cap.get("CGA"));
        CPA = String.valueOf(cap.get("CPA"));
    }
}
