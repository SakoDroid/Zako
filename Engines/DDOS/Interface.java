package Engines.DDOS;

import Server.Utils.Logger;

public class Interface {

    private static Core core;
    private static boolean status = true;

    public static void load(boolean active){
        if (active){
            core = new Core();
            Logger.ilog("DDOS protection is running ...");
        }
        else status = false;
    }

    public static boolean checkIP(String ip,String hostName){
        if (status) return core.trackIP(ip,hostName);
        else return true;
    }

    public static void addReqVol(String ip, long reqSize){
        if (core != null) core.increaseReqVol(ip,reqSize);
    }

    public static void addWarning(String ip, String host){
        if (core != null)
            core.addWarning(ip,host);
    }

    public static void clearRecords(String ip){
        core.clearRecord(ip);
    }
}