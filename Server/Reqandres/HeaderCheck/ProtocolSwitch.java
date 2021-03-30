package Server.Reqandres.HeaderCheck;

import Server.Reqandres.Request.Request;
import Server.Utils.Configs.HTAccess;

class ProtocolSwitch {

    private final Request req;

    public ProtocolSwitch(Request req){
        this.req = req;
        if (isProtoSwitchRequested() && isClientsRequestForProtocolSwitchValid())
            req.setResponseCode(101);
    }

    public boolean isProtoSwitchRequested(){
        Object cnc = req.getHeaders().get("Connection");
        if (cnc != null){
            String connection = String.valueOf(cnc);
            if (connection.equals("upgrade")){
                Object up = req.getHeaders().get("Upgrade");
                return up != null;
            }
        }
        return false;
    }

    public boolean isClientsRequestForProtocolSwitchValid (){
        String upgradeHeader = req.getHeaders().get("Upgrade");
        if (HTAccess.getInstance().isUpAllowed(req.getHost())){
            if (HTAccess.getInstance().isUpgradePermitted(upgradeHeader, req.getHost()) && !upgradeHeader.equalsIgnoreCase("h2")) {
                if (req.getProt().equals("HTTP/1.1"))
                    return !upgradeHeader.equalsIgnoreCase("http/1.1");
                else if (req.getProt().equals("HTTP/2"))
                    return !upgradeHeader.equalsIgnoreCase("h2c");
            }
        }
        return false;
    }
}
