package Engines.FCGI.Request;

import Server.Utils.Logger;
import java.util.Map;

public class FCGIParamsBody extends FCGIRequestComponent {

    public static FCGIParamsBody getInstance(Map<String,String> params){
        return new FCGIParamsBody(params);
    }

    private FCGIParamsBody(Map<String,String> envs){
        this.writeParams(envs);
        this.makeReadyForSend();
    }

    private void writeParams(Map<String,String> params){
        if (params != null && params.size() > 0){
            for (Map.Entry<String,String> entry : params.entrySet()){
                byte[] key = entry.getKey().getBytes();
                byte[] value = entry.getValue().getBytes();
                this.writeSize(key.length);
                this.writeSize(value.length);
                try{
                    out.write(key);
                    out.write(value);
                }catch (Exception ex) {
                    String t = "";
                    for (StackTraceElement a : ex.getStackTrace()) {
                        t += a.toString() + " ;; ";
                    }
                    t += ex.toString();
                    Logger.ilog(t);
                }
            }
        }
    }

    private void writeSize(int Length){
        if (Length < 128) {
            out.write(Length);
        } else {
            out.write((Length >> 24) | 0x80);
            out.write((Length >> 16) & 0xFF);
            out.write((Length >> 8) & 0xFF);
            out.write(Length & 0xFF);
        }
    }

    @Override
    protected void makeReadyForSend(){
        this.isReady = true;
    }
}
