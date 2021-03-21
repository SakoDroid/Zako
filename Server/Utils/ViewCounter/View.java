package Server.Utils.ViewCounter;

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
        if (Server.Utils.Configs.isVCOn(host)
                && Records.getInstance().isValid(ip)){
            Controller.getInstance().getViewCore(host).addView(ip);
            Records.getInstance().addRecord(ip);
        }
    }
}
