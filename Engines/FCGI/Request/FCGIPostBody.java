package Engines.FCGI.Request;

import Server.Utils.Logger;

public class FCGIPostBody extends FCGIRequestComponent {

    private FCGIPostBody(byte[] data){
        try{
            if (data.length > 0) out.write(data);
            this.makeReadyForSend();
        }catch (Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    public static FCGIPostBody getInstance(String body){
        if (body != null) return new FCGIPostBody(body.getBytes());
        else return new FCGIPostBody(new byte[]{});
    }

    @Override
    protected void makeReadyForSend(){
        this.isReady = true;
    }
}
