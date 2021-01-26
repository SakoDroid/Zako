package LoadBalancer;

import Server.Utils.Logger;

import java.io.File;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

public class Configs {

    public static List<String[]> servers = new ArrayList<>();

    public static void load(){
        try{
            File fl = null;
            if (System.getProperty("os.name")
                    .toLowerCase().contains("windows"))
                fl = new File(System.getProperty("user.dir") + "/Configs/Load_Balancer.cfg");
            else if (System.getProperty("os.name")
                    .toLowerCase().contains("linux"))
                fl = new File("/etc/zako-web/Load_Balancer.cfg");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.parse("/etc/zako-web/Load_Balancer.cfg");
            NodeList nl = d.getElementsByTagName("Zako");
            for (int i = 0 ; i < nl.getLength() ; i++){
                Element el = (Element) nl.item(i);
                String host = el.getElementsByTagName("Host").item(0).getTextContent();
                String port = el.getElementsByTagName("Port").item(0).getTextContent();
                if (!host.isEmpty()) servers.add(new String[]{host,((port.isEmpty())?"80":port)});
            }
            Tracker.start();
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }
}
