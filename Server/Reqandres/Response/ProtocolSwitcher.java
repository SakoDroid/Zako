package Server.Reqandres.Response;

import Server.Reqandres.Request.ServerRequest;
import Server.Reqandres.Senders.Sender;
import Server.Utils.HTAccess;

public class ProtocolSwitcher {

    private final ServerRequest request;

    public ProtocolSwitcher(ServerRequest req){
        this.request = req;
        this.initiateProtocolSwitch();
    }

    private void initiateProtocolSwitch(){
        Sender snd = new Sender(request.getProt(),101);
        String up = String.valueOf(request.getHeaders().get("Upgrade"));
        snd.addHeader("Upgrade: " + up.trim());
        snd.setKeepAlive(false);
        snd.send(null,request.out, request.getIP(), request.getID(), request.getHost());
    }

    public static boolean isClientsRequestForProtocolSwitchValid (String upgradeHeader,String hostName,String prot){
        if (HTAccess.getInstance().isUpAllowed(hostName)){
            if (HTAccess.getInstance().isUpgradePermitted(upgradeHeader, hostName)) {
                if (prot.equals("HTTP/1.1"))
                    return !upgradeHeader.equalsIgnoreCase("http/1.1");
                else if (prot.equals("HTTP/2"))
                    return !upgradeHeader.equalsIgnoreCase("h2") && !upgradeHeader.equalsIgnoreCase("h2c") &&
                            !upgradeHeader.equalsIgnoreCase("http/2");
            }
        }
        return false;
    }
}
