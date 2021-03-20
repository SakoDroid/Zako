package Server.Utils;

import java.io.File;
import java.util.HashMap;
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

    public static String getHeaders(String extension,String host){
        String ret = configs.get(host).getHeaders(extension);
        return (ret != null) ? ret : "";
    }

    private static class FTConfig{

        private final HashMap<String,String> cnts = new HashMap<>();
        private final HashMap<String,String> headers = new HashMap<>();

        public FTConfig(File fl){
            this.loadCnts(fl);
            this.loadHeaders(fl);
        }

        private void loadCnts(File fl){
            try{
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(fl.getAbsolutePath() + "/MIME.cfg");
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
            try{
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document d = db.parse(fl.getAbsolutePath() + "/Headers.cfg");
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

        public String getContentType(String ext){
            return this.cnts.get(ext);
        }

        public String getHeaders(String extension){
            String ret = this.headers.get(extension);
            return (ret != null) ? ret : "";
        }
    }
}