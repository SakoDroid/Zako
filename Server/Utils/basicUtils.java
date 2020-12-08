package Server.Utils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class basicUtils {

    private static final File statuses = new File(Configs.getCWD() + "/src/Data/status_codes.sak");
    private static final File cmds = new File (Configs.getCWD() + "/src/Data/cmds.sak");
    private static HashMap<Integer,String> codes;
    private static HashMap<String,String> execCmds;
    private static final ArrayList<Integer> ids = new ArrayList<>();
    private static final Random rnd = new Random();
    public static String LocalHostIP = "";

    private basicUtils(){}

    public static void load(){
        try{
            codes = (HashMap<Integer,String>) new ObjectInputStream(new FileInputStream(statuses)).readObject();
            execCmds = (HashMap<String,String>) new ObjectInputStream(new FileInputStream(cmds)).readObject();
            for (int code : codes.keySet()){
                File fl = new File(Configs.getCWD() + "/src/default_pages/" + code +".html");
                if (!fl.exists() && code > 399){
                    String des = getStatusCode(code);
                    FileWriter fw = new FileWriter(fl);
                    fw.write("""
                            <!DOCTYPE html>
                            <html lang="en">
                                <head>
                                    <meta charset="UTF-8"/>
                                    <meta name="viewport" content="width=device-width initial-scale=1.0"/>
                                    <title>""" + des + """
                            </title>
                                </head>
                                <body>
                                    <div style="margin-top: 20%;font-family: 'Courier'">
                                        <h1 style="margin: auto;width: fit-content;font-size: 100px;">""" + code + """
                            </h1>
                                        <h3 style="margin: auto;width: fit-content;font-size: 30px;">""" + des + """
                            !</h3><br/><br/>
                                    </div>
                                </body>
                            </html>""");
                    fw.flush();
                    fw.close();
                }
            }
            URL url = new URL("http://checkip.amazonaws.com/");
            InputStreamReader bf = new InputStreamReader(url.openStream());
            int i;
            while((i = bf.read()) != -1){
                LocalHostIP += (char)i;
            }
            LocalHostIP = LocalHostIP.strip().replace("\n","");
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    public static String getStatusCode(int code){
        return codes.get(code);
    }

    public static String getStatusCodeComp(int code){
        return String.format("%d %s",code,getStatusCode(code));
    }

    public static String getExecCmd(String ext){
        return execCmds.get(ext);
    }

    public static int getID(){
        int id = Math.abs(rnd.nextInt());
        while (ids.contains(id) || id == 0) {
            id = rnd.nextInt();
        }
        ids.add(id);
        return id;
    }

    public static void delID(int id){
        ids.remove((Integer) id);
    }

}