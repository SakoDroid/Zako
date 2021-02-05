package Engines.DDOS;

import Server.Utils.Logger;

public class Interface {

    private static Core core;
    private static boolean status = true;
    private static long time;

    public static void load(boolean active,long tm){
        if (active){
            core = new Core();
            time = tm;
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
}