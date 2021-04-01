package Server.Reqandres.HeaderCheck;

import Server.Reqandres.Request.Request;
import Server.Utils.Configs.Configs;
import Server.Utils.Configs.HTAccess;
import Server.Utils.SocketsData;
import java.util.regex.*;

public class Connection {
    
    private final Request req;
    
    public Connection(Request req){
        this.req = req;
        this.process();
    }
    
    private void process(){
        String cnc = req.getHeaders().get("Connection");
        if (cnc != null)
            req.setKeepAlive(HTAccess.getInstance().isKeepAliveAllowed(req.getHost()) && cnc.trim().equals("keep-alive"));
        if (req.getKeepAlive()){
            Object kaHeader = req.getHeaders().get("Keep-Alive");
            if (kaHeader != null){
                String header = String.valueOf(kaHeader);
                Pattern timeoutPtn = Pattern.compile("timeout=\\d+");
                Matcher timeoutMc = timeoutPtn.matcher(header);
                Pattern maxPtn = Pattern.compile("max=\\d+");
                Matcher maxMc = maxPtn.matcher(header);
                if (timeoutMc.find())
                    req.setTimeout(Integer.parseInt(timeoutMc.group().replace("timeout=","").trim()));
                if(maxMc.find())
                    SocketsData.getInstance().setMaxReqsPerSock(req.getSocket(),Integer.parseInt(maxMc.group().replace("max=","").trim()));
            }else
                req.setTimeout(HTAccess.getInstance().getKeepAliveTimeout(req.getHost()));
        }else
            req.setTimeout(Configs.getTimeOut(req.getHost()));
    }
}
