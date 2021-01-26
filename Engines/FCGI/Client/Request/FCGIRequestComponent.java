package Engines.FCGI.Client.Request;

import Engines.FCGI.Client.Utils.ComponentNotReadyException;
import Server.Utils.Logger;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

abstract class FCGIRequestComponent {

    protected final ByteArrayOutputStream out = new ByteArrayOutputStream();
    protected boolean isReady = false;

    protected abstract void makeReadyForSend();

    public int getLength(){
        return out.size();
    }

    public void send(OutputStream os) throws ComponentNotReadyException {
        if (isReady){
            try {
                if(out.size() > 0){
                    out.writeTo(os);
                    out.flush();
                    out.close();
                }
            } catch (Exception ex) {
                Logger.logException(ex);
            }
        }else{
            throw new ComponentNotReadyException(this.getClass().getName());
        }
    }
}
