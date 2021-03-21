package Server.HttpAuth;

import Server.Reqandres.Request.Request;
import Server.Utils.Configs;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

public class Interface {

    private final HashMap<String,Core> cores = new HashMap<>();
    private static final Interface inf = new Interface();

    private Interface(){
        File fl = new File(Configs.baseAddress);
        for (String li : Objects.requireNonNull(fl.list())) {
            File dir = new File(Configs.baseAddress + "/li");
            if (dir.isDirectory())
                cores.put(li,new Core(dir));
        }
    }

    public boolean needAuth(String path,String host){
        return cores.get(host).apiContains(path);
    }

    public int evaluate(HashMap headers,String ip,String host){
        int temp;
        Object auth = headers.get("Authorization");
        if (auth != null){
            temp = cores.get(host).checkAuth(String.valueOf(auth),ip);
        }else
            temp = 401;
        return temp;
    }

    public void send401(Request req){
        cores.get(req.getHost()).askForAuth(req);
    }

    public static Interface getInstance(){
        return inf;
    }
}
