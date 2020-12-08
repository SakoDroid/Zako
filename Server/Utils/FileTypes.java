package Server.Utils;

import java.io.FileInputStream;
import java.util.HashMap;
import java.io.ObjectInputStream;

public class FileTypes {

    private static HashMap<String,String> cnts;


    private FileTypes(){}

    public static String getContentType(String ext){
        try{
            if (cnts == null) cnts = (HashMap<String,String>)new ObjectInputStream(new FileInputStream(Configs.getCWD() + "/src/Data/content-types.sak")).readObject();
        }catch (Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
        return cnts.get(ext);
    }
}