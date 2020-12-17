package Server.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.*;

public class SSLConfigs {

    private static String jks;
    private static String pss;

    public static void load(){
        try(BufferedReader bf = new BufferedReader(new FileReader(Configs.getCWD() + "/src/CFGS/ssl.cfg"))){
            String line;
            String cfgs = "";
            while((line = bf.readLine()) != null){
                if (!line.startsWith("#")) cfgs += line + "\n";
            }
            Pattern ptn = Pattern.compile("jks-path=.*");
            Matcher mc = ptn.matcher(cfgs);
            if (mc.find()) jks = mc.group().replace("jks-path=","").trim();
            ptn = Pattern.compile("jks-pass=.*");
            mc = ptn.matcher(cfgs);
            if (mc.find()) pss = mc.group().replace("jks-pass=","").trim();
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    public static String getJKS(){
        return jks;
    }

    public static String getPass(){
        return pss;
    }
}
