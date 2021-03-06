package Server.HttpAuth;

import Server.Reqandres.Request.Request;
import Server.Reqandres.Senders.Sender;
import Server.Utils.JSON.JSONBuilder;
import Server.Utils.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.regex.*;

public class Core {

    private final ArrayList<Pattern> apis = new ArrayList<>();
    private final HashMap<String,String> passwd = new HashMap<>();
    private final HashMap<String,String> opaques = new HashMap<>();
    private final HashSet<String> stales = new HashSet<>();
    private final File fl;
    private Mechanisms Mechanism;
    private String alg;
    private String realm;

    public Core (File fl){
        this.fl = fl;
        this.load();
    }

    private void load(){
        loadPasswd();
        HashMap data = (HashMap) JSONBuilder
                .newInstance()
                .parse(new File(fl.getAbsolutePath() + "/Main.conf"))
                .toJava();
        HashMap authCfg = (HashMap) data.get("HTTP AUTH");
        String mech = String.valueOf(authCfg.get("Auth mechanism"));
        this.realm = String.valueOf(authCfg.get("Auth realm"));
        if (mech.contains(" ")){
            Mechanism = Mechanisms.Digest;
            alg = mech.split(" ",2)[1];
        }else
            Mechanism = Mechanisms.Basic;
        ArrayList<String> needs = (ArrayList<String>) authCfg.get("Need");
        for (String s : needs)
            apis.add(Pattern.compile(s));
        new PasswdUpdater();
    }

    private void loadPasswd(){
        try {
            try(BufferedReader bf = new BufferedReader(new FileReader(fl.getAbsolutePath() + "/sec/passwd"))) {
                String line;
                while ((line = bf.readLine()) != null) {
                    if (!line.startsWith("#") && !line.isEmpty()) {
                        String[] entry = line.split(":");
                        if (entry.length > 1)
                            passwd.put(entry[0].trim(), entry[1].trim());
                    }
                }
            }
        } catch (Exception ex){
            Logger.logException(ex);
        }
    }

    public boolean apiContains(String api){
        Matcher mc;
        for (Pattern ptn : apis){
            mc = ptn.matcher(api);
            if (mc.find())
                return true;
        }
        return false;
    }

    public int checkAuth(String authorization,String ip){
        String[] authHeader = authorization.split(" ",2);
        int temp = 403;
        if (authHeader.length > 1){
            if (authHeader[0].equals("Basic"))
                temp = this.checkBasic(authHeader[1]) ? 200 : 403;
            else if (authHeader[0].equals("Digest")){
                temp = this.checkDigest(authHeader[1],ip);
            }
        }
        return temp;
    }

    private boolean checkBasic(String data){
        byte[] decoded = Base64.getDecoder().decode(data);
        String entry = new String(decoded);
        String[] usPass = entry.split(":",2);
        if (usPass.length > 1){
            String pass = passwd.get(usPass[0]);
            if (pass != null)
                return pass.equals(usPass[1]);
            else
                return false;
        }else
            return false;
    }

    private int checkDigest(String data,String ip){
        int temp = new Digest(data,ip).getSituation();
        if (temp == 0)
            return 200;
        else if (temp == 1){
            this.stales.add(ip);
            return 401;
        }
        else
            return  403;
    }

    public void askForAuth(Request req){
        Server.Reqandres.Senders.Sender snd = new Sender(req.getProt(),401);
        snd.setKeepAlive(req.getKeepAlive());
        snd.addHeader("WWW-Authenticate", getHeader(req));
        snd.send(null,req);
    }

    private String getHeader(Request req){
        String header = Mechanism + " realm=\"" + realm + "\"";
        if (Mechanism == Mechanisms.Digest){
            header += ",algorithm=" + alg;
            header += ",nonce=\"" + this.genNonce(req.getIP()) + "\"";
            header += ",opaque=\"" + this.genOpaque(req.getIP()) + "\"";
            if (stales.contains(req.getIP())){
                stales.remove(req.getIP());
                header += ",stale=false";
            }
        }
        return header;
    }

    private String genNonce(String ip){
        String nonce = new Date().getTime() + " " + ip;
        return Base64.getEncoder().encodeToString(nonce.getBytes());
    }

    private String genOpaque(String ip){
        String temp = UUID.randomUUID().toString();
        opaques.put(ip,temp);
        return Base64.getEncoder().encodeToString(temp.getBytes());
    }

    private class Digest {

        //0 is OK , 1 is stale, 2 is 403.
        private int situation = 2;
        private final HashMap<String,String> items = new HashMap<>();

        public Digest(String authorizationHeader,String ip){
            this.parseHeader(authorizationHeader);
            this.validate(ip);
        }

        private void parseHeader(String header){
            Pattern ptn = Pattern.compile("username=\"[^\"]+");
            Matcher mc = ptn.matcher(header);
            if (mc.find())
                items.put("username",mc.group().replace("username=\"","").trim());
            ptn = Pattern.compile("nonce=\"[^\"]+");
            mc = ptn.matcher(header);
            if (mc.find())
                items.put("nonce",mc.group().replace("nonce=\"","").trim());
            ptn = Pattern.compile("opaque=\"[^\"]+");
            mc = ptn.matcher(header);
            if (mc.find())
                items.put("opaque",mc.group().replace("opaque=\"","").trim());
            ptn = Pattern.compile("response=\"[^\"]+");
            mc = ptn.matcher(header);
            if (mc.find())
                items.put("response",mc.group().replace("response=\"","").trim());
            ptn = Pattern.compile("algorithm=[^,]+");
            mc = ptn.matcher(header);
            if (mc.find())
                items.put("algorithm",mc.group().replace("algorithm=","").trim());
        }

        private void validate(String ip){
            String servPass = passwd.get(this.items.get("username"));
            String clientPass = this.items.get("response");
            int nonceCheckRes = checkNonce(this.items.get("nonce"),ip);
            int opaqueCheckRes = checkOpaque(this.items.get("opaque"),ip);
            if (nonceCheckRes == 0){
                if (opaqueCheckRes == 0){
                    if (servPass != null){
                        if (servPass.equals(clientPass)){
                            situation = 0;
                        }
                    }
                }else
                    situation = opaqueCheckRes;
            }else
                situation = nonceCheckRes;
        }

        //0 is ok, 1 is stale , 2 is 403.
        private int checkOpaque(String opaque, String ip){
            String so = opaques.get(ip);
            if (so != null){
                if (new String(Base64.getDecoder().decode(opaque)).equals(so)){
                    opaques.remove(ip);
                    return 0;
                }else
                    return 2;
            }else
                return 1;
        }

        //0 is ok, 1 is stale , 2 is 403.
        private int checkNonce(String nonce,String ip){
            String decoded = new String(Base64.getDecoder().decode(nonce));
            String[] items = decoded.split(" ");
            if (ip.equals(items[1]))
                if (new Date().getTime() - Long.parseLong(items[0]) < 10000)
                    return 0;
                else
                    return 1;
            else
                return 2;
        }

        public int getSituation(){
            return this.situation;
        }
    }

    private class PasswdUpdater extends Thread{

        public PasswdUpdater(){
            this.start();
        }

        @Override
        public void run(){
            javax.swing.Timer t  = new javax.swing.Timer(2000, e -> loadPasswd());
            java.util.Timer tt = new java.util.Timer(false);
            tt.schedule(new TimerTask() {
                @Override
                public void run() {
                    t.start();
                }
            },0);
        }
    }
}