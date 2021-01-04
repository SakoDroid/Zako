package Engines.FCGI.Request;

import Server.Utils.Logger;

public class FCGIBeginRequestBody extends FCGIRequestComponent {

    private final int roleB1;
    private final int roleB0;
    private final int flag;
    private final byte[] reserved;

    private FCGIBeginRequestBody(int roleB1,int roleB0,int flag,byte[] reserved){
        this.roleB0 = roleB0;
        this.roleB1 = roleB1;
        this.flag = flag;
        this.reserved = reserved;
        this.makeReadyForSend();
    }

    public static FCGIBeginRequestBody getInstance(int role,int flag){
        return new FCGIBeginRequestBody((byte)((role >> 8) & 0xFF),
                (byte) (role & 0xff),
                flag,
                new byte[]{0,0,0,0,0}
        );
    }

    public static FCGIBeginRequestBody getInstance(int role,int flag,byte[] reserved){
        return new FCGIBeginRequestBody((byte)((role >> 8) & 0xFF),
                (byte) (role & 0xff),
                flag,
                reserved
        );
    }

    @Override
    protected void makeReadyForSend(){
        try{
            out.write((byte) roleB1);
            out.write((byte) roleB0);
            out.write((byte) flag);
            out.write(reserved);
            isReady = true;
        }catch (Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
            isReady = false;
        }
    }
}
