package LoadBalancer;

import Server.Utils.Logger;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Piper extends Thread{

    private final InputStream in;
    private final OutputStream out;
    private final Socket connection1;
    private final Socket connection2;

    public Piper(InputStream is, OutputStream os, Socket s1,Socket s2){
        this.in = is;
        this.out = os;
        this.connection1 = s1;
        this.connection2 = s2;
        this.start();
    }

    @Override
    public void run(){
        try{
            in.transferTo(out);
        }catch(Exception ex){
            try{
                connection1.close();
                connection2.close();
            }catch (Exception ignored){}
            Logger.logException(ex);
        }
    }
}
