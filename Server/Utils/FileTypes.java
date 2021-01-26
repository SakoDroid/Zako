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
            File fl = null;
            if (System.getProperty("os.name")
                    .toLowerCase().contains("windows"))
                fl = new File(System.getProperty("user.dir") + "/Configs/MIME.cfg");
            else if (System.getProperty("os.name")
                    .toLowerCase().contains("linux"))
                fl = new File("/etc/zako-web/MIME.cfg");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(fl);
            NodeList mms = doc.getElementsByTagName("MIME");
            for (int i = 0 ; i < mms.getLength() ; i++){
                Element mm = (Element) mms.item(i);
                addCT(mm.getElementsByTagName("extension").item(0).getTextContent(),mm.getElementsByTagName("MIME-Type").item(0).getTextContent());
            }
        }catch (Exception ex){
            Logger.logException(ex);
        }
    }

    private static void loadHeaders(){
        try{
            File fl = null;
            if (System.getProperty("os.name")
                    .toLowerCase().contains("windows"))
                fl = new File(System.getProperty("user.dir") + "/Configs/Headers.cfg");
            else if (System.getProperty("os.name")
                    .toLowerCase().contains("linux"))
                fl = new File("/etc/zako-web/Headers.cfg");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.parse(fl);
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
            Logger.logException(ex);
        }
    }

    public static void load(){
        loadCnts();
        loadHeaders();
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