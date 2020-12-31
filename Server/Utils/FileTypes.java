package Server.Utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.io.ObjectInputStream;

public class FileTypes {

    private static HashMap<String,String> cnts;


    private FileTypes(){}

    public static void load(){
        try(BufferedReader bf = new BufferedReader(new FileReader(Configs.getCWD() + "/CFGS/MIME.cfg"))){
            cnts = (HashMap<String,String>)new ObjectInputStream(new FileInputStream(Configs.getCWD() + "/Data/content-types.sak")).readObject();
            String line;
            while((line = bf.readLine()) != null){
                if (!line.startsWith("#") && !line.isEmpty()){
                    String[] tmp = line.split(":",2);
                    addCT(tmp[0].trim(),tmp[1].trim());
                }
            }
        }catch (Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    private static void addCT(String ext,String ct){
        if (cnts.get(ext) == null){
            cnts.put(ext.trim(),ct.trim());
        }else{
            cnts.replace(ext.trim(),ct.trim());
        }
    }

    public static String getContentType(String ext){
        return cnts.get(ext);
    }
}