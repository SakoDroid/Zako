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
        if (Server.Utils.Configs.ViewCounter
                && Records.getInstance().isValid(ip)){
            ViewCore vc = Controller.getInstance().getViewCore(host);
            vc.addView(ip);
            Records.getInstance().addRecord(ip);
        }
    }
}
