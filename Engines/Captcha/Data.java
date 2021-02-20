package Engines.Captcha;

import Server.Utils.CaptchaConfigs;
import java.util.HashMap;
import java.util.regex.*;

public class Data {

    private final static HashMap<String,String> ipAns = new HashMap<>();

    public static void addRecord(String ip,String ans){
        ipAns.put(ip,((CaptchaConfigs.UCS) ? ans : ans.toLowerCase()));
        new RecordDeleter().start(ipAns,ip);
    }

    public static String checkAnswer(String ip,String postBody){
        String temp = "FLS";
        if(!postBody.isEmpty()){
            Pattern ptn = Pattern.compile("Ans=[^&]+");
            Matcher mc = ptn.matcher(postBody);
            String ans = "";
            if (mc.find()) ans = mc.group().replace("Ans=", "");
            if (!ans.isEmpty()) {
                String an = ipAns.get(ip);
                if (an != null){
                    if (an.equals(
                            ((CaptchaConfigs.UCS) ? ans : ans.toLowerCase())
                    )){
                        temp = "OK";
                        ipAns.remove(ip);
                    }
                }
                else temp = "NA";
            }
        }
        return temp;
    }
}