package Server.Captcha;

import java.util.HashMap;
import java.util.regex.*;

public class Data {

    private final static HashMap<String,String> ipans = new HashMap<>();

    public static void addRecord(String ip,String ans){
        ipans.put(ip,ans.toLowerCase());
    }

    public static String checkAnswer(String ip,String postBody){
        String temp = "FLS";
        if(!postBody.isEmpty()){
            Pattern ptn = Pattern.compile("Ans=[^&]+");
            Matcher mc = ptn.matcher(postBody);
            String ans = "";
            if (mc.find()) ans = mc.group().replace("Ans=", "");
            if (!ans.isEmpty()) {
                if (ipans.get(ip).equals(ans.toLowerCase())) temp = "OK";
            }
        }
        return temp;
    }
}