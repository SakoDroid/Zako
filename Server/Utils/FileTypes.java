package Server.Utils;

import java.io.File;
import java.util.HashMap;
import org.w3c.dom.*;
import javax.xml.parsers.*;

public class FileTypes {

    private static final HashMap<String,String> cnts = new HashMap<>();
    private static final HashMap<String,String> headers = new HashMap<>();

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
            NodeList rules = d.getElementsByTagName("rule");
            for (int i = 0 ; i < rules.getLength() ; i++){
                Element rule = (Element) rules.item(i);
                String ex = rule.getAttribute("ext");
                String[] exs = ex.split(",");
                StringBuilder header = new StringBuilder();
                header.append('\n');
                NodeList child = rule.getChildNodes();
                for (int j = 0 ; j < child.getLength() ; j++){
                    Node hd = child.item(j);
                    if (hd.getNodeName().equals("#text"))
                        continue;
                    header.append(hd.getNodeName()).append(": ").append(hd.getTextContent());
                }
                for (String ext : exs)
                    headers.put(ext.trim(),header.toString());
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

    public static String getHeaders(String extension){
        String ret = headers.get(extension);
        return (ret != null) ? ret : "";
    }
}