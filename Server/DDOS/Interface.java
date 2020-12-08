package Server.DDOS;

import Server.Utils.Logger;

public class Interface {

    private static Core core;
    private static boolean status = true;
    private static long time;

    public static void load(int active,long tm){
        if (active == 1){
            core = new Core();
            time = tm;
            Logger.ilog("DDOS protection is running ...");
        }
        else status = false;
    }

    public static boolean checkIP(String ip,long reqSize){
        if (status) return core.trackIP(ip,reqSize);
        else return true;
    }
}