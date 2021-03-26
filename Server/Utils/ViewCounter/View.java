package Server.Utils.ViewCounter;

import Server.Utils.Configs.Configs;

public class View extends Thread{

    private final String host;
    private final String ip;

    public View (String hostName, String IP){
        this.host = hostName;
        this.ip = IP;
        this.start();
    }

    @Override
    public void run(){
        if (Configs.isVCOn(host)
                && Records.getInstance().isValid(ip)){
            Controller.getInstance().getViewCore(host).addView(ip);
            Records.getInstance().addRecord(ip);
        }
    }
}
