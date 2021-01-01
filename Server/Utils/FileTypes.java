package Server.Utils;

import java.io.File;
import java.util.HashMap;
import org.w3c.dom.*;
import javax.xml.parsers.*;

public class FileTypes {

    private static final HashMap<String,String> cnts = new HashMap<>();
    private static final HashMap<String,String> cache = new HashMap<>();

    private FileTypes(){}

    private static void loadCnts(){
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(Configs.getCWD() + "/CFGS/MIME.cfg"));
            NodeList mms = doc.getElementsByTagName("MIME");
            for (int i = 0 ; i < mms.getLength() ; i++){
                Element mm = (Element) mms.item(i);
                addCT(mm.getElementsByTagName("extension").item(0).getTextContent(),mm.getElementsByTagName("MIME-Type").item(0).getTextContent());
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

    private static void loadCache(){
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.parse(new File(Configs.getCWD() + "/CFGS/Headers.cfg"));
            Element ch = (Element) d.getElementsByTagName("Cache").item(0);
            NodeList rules = ch.getElementsByTagName("rule");
            for (int i = 0 ; i < rules.getLength() ; i++){
                Element rule = (Element) rules.item(i);
                String exts = rule.getElementsByTagName("extensions").item(0).getTextContent();
                String age = rule.getElementsByTagName("value").item(0).getTextContent().trim();
                for (String ext : exts.split(",")) cache.put(ext.trim(),age);
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

    public static void load(){
        loadCnts();
        loadCache();
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

    public static String getAge(String ext){
        if (Configs.cache){
            String value = cache.get(ext);
            return ((value == null) ? "no-store" : value);
        }else return "no-store";
    }
}