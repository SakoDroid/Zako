package Server.Utils.Configs;

import java.io.File;
import java.util.HashMap;
import Server.Utils.JSON.JSONBuilder;
import Server.Utils.Logger;
import org.w3c.dom.*;
import javax.xml.parsers.*;

public class FileTypes {

    private static final HashMap<String,FTConfig> configs = new HashMap<>();

    private FileTypes(){}

    public static void load(File fl){
        configs.put(fl.getName(),new FTConfig(fl));
    }

    public static String getContentType(String ext,String host){
        return configs.get(host).getContentType(ext);
    }

    public static HashMap<String,String> getHeaders(String extension,String host){
        return configs.get(host).headers.get(extension);
    }

    private static class FTConfig{

        private final HashMap<String,String> cnts = new HashMap<>();
        private final HashMap<String,HashMap<String,String>> headers = new HashMap<>();

        public FTConfig(File fl){
            this.loadCnts(fl);
            this.loadHeaders(fl);
        }

        private void loadCnts(File fl){
            try{
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(fl.getAbsolutePath() + "/MIME.conf");
                NodeList mms = doc.getElementsByTagName("MIME");
                for (int i = 0 ; i < mms.getLength() ; i++){
                    Element mm = (Element) mms.item(i);
                    cnts.put(mm.getElementsByTagName("extension").item(0).getTextContent(),mm.getElementsByTagName("MIME-Type").item(0).getTextContent());
                }
            }catch (Exception ex){
                Logger.logException(ex);
            }
        }

        private void loadHeaders(File fl){
            HashMap mainData = (HashMap) JSONBuilder.newInstance().parse(new File(fl.getAbsolutePath() + "/Headers.conf")).toJava();
            HashMap exts = (HashMap) mainData.get("Ext");
            for (Object key : exts.keySet()){
                HashMap<String,String> data = (HashMap<String, String>) exts.get(key);
                for (String ext : String.valueOf(key).split(" "))
                    headers.put(ext,data);
            }
            HashMap files = (HashMap) mainData.get("File");
            for (Object key : files.keySet()){
                HashMap<String,String> data = (HashMap<String, String>) files.get(key);
                String keyv = key.toString();
                File flkey = new File(keyv);
                headers.put(keyv,data);
                headers.put(Configs.getCWD() + "/Cache/Compressed/" + flkey.getName() + ".df",data);
                headers.put(Configs.getCWD() + "/Cache/Compressed/" + flkey.getName() + ".gz",data);
            }
        }

        public String getContentType(String ext){
            return this.cnts.get(ext);
        }
    }
}