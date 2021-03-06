package LoadBalancer;

import Server.Utils.Logger;
import java.io.InputStream;
import java.net.Socket;

class Tracker {

    public static String[] firstServer ;

    private static class track extends Thread{

        private String[] bestServer;
        private long curen = 0;
        private final Object obj = new Object();

        public track(){
            this.start();
        }

        @Override
        public void run(){
            try{
                synchronized (obj){
                    while(true){
                        for (String[] sv : Configs.servers){
                            Socket s = new Socket(sv[0],8560);
                            InputStream in = s.getInputStream();
                            String res = "";
                            int i;
                            while((i = in.read()) != -1) res += (char)i;
                            long serverMemory = Long.parseLong(res.trim());
                            if (serverMemory > this.curen){
                                bestServer = sv;
                                this.curen = serverMemory;
                            }
                        }
                        firstServer = bestServer;
                        obj.wait(100);
                    }
                }
            }catch(Exception ex){
                Logger.logException(ex);
            }
        }
    }

    public static void start(){
        Logger.ilog("Servers tracker started ...");
        new track();
    }

    public static String[] getServer(){
        return firstServer;
    }

}
