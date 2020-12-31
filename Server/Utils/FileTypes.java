package Server.Utils;

import java.io.File;
import java.util.HashMap;
import org.w3c.dom.*;
import javax.xml.parsers.*;

public class FileTypes {

    private static final HashMap<String,String> cnts = new HashMap<>();


    private FileTypes(){}

    public static void load(){
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