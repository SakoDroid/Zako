package Server.Utils;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Senders.FileSender;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class basicUtils {

    private static final File statuses = new File(Configs.getCWD() + "/Data/status_codes.sak");
    private static final File cmds = new File (Configs.getCWD() + "/Data/cmds.sak");
    private static HashMap<Integer,String> codes;
    private static HashMap<String,String> execCmds;
    private static final ArrayList<Integer> ids = new ArrayList<>();
    private static final Random rnd = new Random();
    public static String LocalHostIP = "", Zako = "Zako 0.8";

    private basicUtils(){}

    public static void load(){
        try{
            codes = (HashMap<Integer,String>) new ObjectInputStream(new FileInputStream(statuses)).readObject();
            execCmds = (HashMap<String,String>) new ObjectInputStream(new FileInputStream(cmds)).readObject();
            for (int code : codes.keySet()){
                File fl = new File(Configs.getCWD() + "/default_pages/" + code +".html");
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

    public static byte[] toByteArray(ArrayList<Byte> bytes){
        byte[] temp = new byte[bytes.size()];
        int i = 0;
        for (Byte b : bytes)
            temp[i++] = b;
        return temp;
    }

    public static void delID(int id){
        ids.remove((Integer) id);
    }

    public static void sendCode(int code, Request req){
        FileSender fs = new FileSender(req.getProt(),code);
        fs.setContentType("text/html");
        fs.setExtension(".html");
        fs.sendFile(Methods.GET,new File(Configs.getCWD() + "/default_pages/" + code + ".html"),req.out,req.getIP(),req.getID(),"NA");
    }

    public static void redirect(int code,String location, Request req){
        Logger.glog("Redirecting " + req.getIP() + " to " + location + "  ; id = " + req.getID(),req.getHost());
        try{
            req.out.writeBytes("HTTP/1.1 " + getStatusCodeComp(code) + "\nServer: " + basicUtils.Zako + "\nLocation: " + location + "\nConnection: close\n\n");
            req.out.flush();
            req.out.close();
            basicUtils.delID(req.getID());
            Logger.glog(req.getIP() + "'s request redirected to " + location + "!" + "  ; id = " + req.getID(),"NA");
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    public static void killPrcs(){
        ArrayList<String> commands;
        String platform = System.getProperty("os.name");
        try{
            if (Configs.isLBOn()){
                commands = getCheckCmd(Configs.getLBPort());
                ProcessBuilder pb = new ProcessBuilder(commands);
                Process p = pb.start();
                InputStream in = p.getInputStream();
                String prcs = new String(in.readAllBytes());
                if (!prcs.isEmpty()){
                    if (platform.equalsIgnoreCase("linux"))
                        killLinux(Configs.getLBPort());
                    else if (platform.equalsIgnoreCase("windows"))
                        killWindows(prcs);
                }
            }
            if (Configs.isWSOn()){
                commands = getCheckCmd(Configs.getWSPort());
                ProcessBuilder pb = new ProcessBuilder(commands);
                Process p = pb.start();
                InputStream in = p.getInputStream();
                String prcs = new String(in.readAllBytes());
                if (!prcs.isEmpty()){
                    if (platform.equalsIgnoreCase("linux"))
                        killLinux(Configs.getWSPort());
                    else if (platform.equalsIgnoreCase("windows"))
                        killWindows(prcs);
                }
            }
            commands = getCheckCmd(8560);
            ProcessBuilder pb = new ProcessBuilder(commands);
            Process p = pb.start();
            InputStream in = p.getInputStream();
            String prcs = new String(in.readAllBytes());
            if (!prcs.isEmpty()){
                if (platform.equalsIgnoreCase("linux"))
                    killLinux(8560);
                else if (platform.equalsIgnoreCase("windows"))
                    killWindows(prcs);
            }
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    private static void killLinux(int port){
        try{
            Runtime.getRuntime().exec((String[]) getKillCmdUbuntu(port).toArray());
            Logger.ilog("Process on port " + port + " has been killed.");
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    private static void killWindows(String result){
        try{
            Pattern ptn = Pattern.compile(" \\d+[^.]");
            Matcher mc = ptn.matcher(result);
            if (mc.find()) {
                Runtime.getRuntime().exec((String[]) getKillCmdWin(mc.group()).toArray());
                Logger.ilog("Process (PID:) " + result + " has been killed.");
            }
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    private static ArrayList<String> getCheckCmd(int port){
        ArrayList<String> cmd = new ArrayList<>();
        String platform = System.getProperty("os.name");
        if (platform.equalsIgnoreCase("linux")){
            cmd.add("fuser");
            cmd.add(port + "/tcp");
        }
        else if (platform.equalsIgnoreCase("windows")){
            cmd.add("netstat");
            cmd.add("-ano");
            cmd.add("|");
            cmd.add("findstr :" + port);
        }
        return cmd;
    }

    private static ArrayList<String> getKillCmdUbuntu(int port){
        ArrayList<String> cmd = new ArrayList<>();
        cmd.add("fuser");
        cmd.add("-k");
        cmd.add(port + "/tcp");
        return cmd;
    }

    private static ArrayList<String> getKillCmdWin(String PID){
        ArrayList<String> cmd = new ArrayList<>();
        cmd.add("taskkill");
        cmd.add("/PID");
        cmd.add(PID);
        cmd.add("/F");
        return cmd;
    }
}