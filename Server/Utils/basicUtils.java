package Server.Utils;

import Server.Reqandres.Request.Request;
import Server.Utils.Configs.Configs;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class basicUtils {

    private static final File statuses = new File(Configs.getCWD() + "/Data/status_codes.sak");
    private static final File cmds = new File (Configs.getCWD() + "/Data/cmds.sak");
    private static HashMap<Integer,String> codes;
    private static HashMap<String,String> execCmds;
    public static String LocalHostIP = "", Zako = "Zako 1.4.2";

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
            Logger.logException(ex);
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

    public static byte[] toByteArray(ArrayList<Byte> bytes){
        byte[] temp = new byte[bytes.size()];
        int i = 0;
        for (Byte b : bytes)
            temp[i++] = b;
        return temp;
    }

    public static void redirect(int code,String location, Request req){
        Logger.glog("Redirecting " + req.getIP() + " to " + location + "  ; id = " + req.getID(),req.getHost());
        try{
            req.out.writeBytes("HTTP/1.1 " + getStatusCodeComp(code) + "\nServer: " + basicUtils.Zako + "\nLocation: " + location + "\nConnection: close\n\n");
            req.out.flush();
            req.out.close();
            Logger.glog(req.getIP() + "'s request redirected to " + location + "!" + "  ; id = " + req.getID(),req.getHost());
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    public static boolean isProtAllowed(String prot){
        return prot.equalsIgnoreCase("http/1.1") || prot.equalsIgnoreCase("h2c") || prot.equalsIgnoreCase("h2")
                || prot.equalsIgnoreCase("http/2");
    }

    public static void killPrcs(){
        for (String host : Configs.getPorts().keySet())
            killPrc(host);
    }

    public static void killPrc(String host){
        ArrayList<String> commands;
        String platform = System.getProperty("os.name");
        try{
            if (Configs.isWSOn()){
                commands = getCheckCmd(Configs.getPorts().get(host));
                ProcessBuilder pb = new ProcessBuilder(commands);
                Process p = pb.start();
                InputStream in = p.getInputStream();
                String prcs = new String(in.readAllBytes());
                if (!prcs.isEmpty()){
                    if (platform.toLowerCase().contains("linux"))
                        killLinux(Configs.getPorts().get(host));
                    else if (platform.toLowerCase().contains("windows"))
                        killWindows(prcs);
                }
            }
            commands = getCheckCmd(8560);
            ProcessBuilder pb = new ProcessBuilder(commands);
            Process p = pb.start();
            InputStream in = p.getInputStream();
            String prcs = new String(in.readAllBytes());
            if (!prcs.isEmpty()){
                if (platform.toLowerCase().contains("linux"))
                    killLinux(8560);
                else if (platform.toLowerCase().contains("windows"))
                    killWindows(prcs);
            }
            if (LoadBalancer.Configs.on){
                commands = getCheckCmd(LoadBalancer.Configs.port);
                ProcessBuilder pbl = new ProcessBuilder(commands);
                Process pl = pbl.start();
                InputStream inl = pl.getInputStream();
                String prcsl = new String(inl.readAllBytes());
                if (!prcsl.isEmpty()){
                    if (platform.toLowerCase().contains("linux"))
                        killLinux(LoadBalancer.Configs.port);
                    else if (platform.toLowerCase().contains("windows"))
                        killWindows(prcsl);
                }
            }
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    private static void killLinux(int port){
        try{
            Runtime.getRuntime().exec((String[]) getKillCmdUbuntu(port).toArray());
            Logger.ilog("Process on port " + port + " has been killed.");
        }catch(Exception ex){
            Logger.logException(ex);
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
            Logger.logException(ex);
        }
    }

    private static ArrayList<String> getCheckCmd(int port){
        ArrayList<String> cmd = new ArrayList<>();
        String platform = System.getProperty("os.name");
        if (platform.toLowerCase().contains("linux")){
            cmd.add("fuser");
            cmd.add(port + "/tcp");
        }
        else if (platform.toLowerCase().contains("windows")){
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