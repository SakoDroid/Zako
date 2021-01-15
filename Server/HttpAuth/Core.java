package Server.HttpAuth;

import Server.Reqandres.Request;
import Server.Reqandres.Senders.FileSender;
import Server.Reqandres.Senders.Sender;
import Server.Utils.JSON.JSONBuilder;
import Server.Utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Core {

    private ArrayList<String> apis;
    private final HashMap<String,String> passwd = new HashMap<>();
    private String header = "WWW-Authenticate: ";

    public Core (){
        this.load();
    }

    private void load(){
        try(BufferedReader bf = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/CFGS/passwd"))){
            String line;
            while((line = bf.readLine()) != null){
                if (!line.startsWith("#") && !line.isEmpty()){
                    String[] entry = line.split(":");
                    if (entry.length > 1)
                        passwd.put(entry[0],entry[1]);
                }
            }
            HashMap data = (HashMap) JSONBuilder
                    .newInstance()
                    .parse(new File(System.getProperty("user.dir") + "/CFGS/Zako.cfg"))
                    .toJava();
            HashMap authCfg = (HashMap) data.get("HTTP AUTH");
            String mech = String.valueOf(authCfg.get("Auth mechanism"));
            String realm = String.valueOf(authCfg.get("Auth realm"));
            header += mech + " realm=\"" + realm + "\"";
            apis = (ArrayList<String>) authCfg.get("Dirs");
        }catch (Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    public boolean apiContains(String api){
        return apis.contains(api);
    }

    public boolean checkAuth(String authorization){
        String[] authHeader = authorization.split(" ",2);
        boolean temp = false;
        if (authHeader.length > 1){
            if (authHeader[0].equals("Basic"))
                temp = this.checkBasic(authHeader[1]);
            else if (authHeader[0].equals("Digest"))
                temp = this.checkDigest(authHeader[1]);
        }
        return temp;
    }

    private boolean checkBasic(String data){
        byte[] decoded = Base64.getDecoder().decode(data);
        String entry = new String(decoded);
        String[] usPass = entry.split(":",2);
        if (usPass.length > 1){
            String pass = passwd.get(usPass[0]);
            if (pass != null) {
                return pass.equals(usPass[1]);
            } else
                return false;
        }else
            return false;
    }

    private boolean checkDigest(String data){
        return false;
    }

    public void askForAuth(Request req){
        Server.Reqandres.Senders.Sender snd = new Sender(req.getProt(),401);
        snd.setKeepAlive(false);
        snd.addHeader(header);
        snd.send(null,req.out,req.getIP(),req.getID(),req.getHost());
    }
}
