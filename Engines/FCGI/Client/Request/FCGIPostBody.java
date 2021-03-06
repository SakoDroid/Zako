package Engines.FCGI.Client.Request;

import Server.Utils.Logger;

public class FCGIPostBody extends FCGIRequestComponent {

    private FCGIPostBody(byte[] data){
        try{
            if (data.length > 0) out.write(data);
            this.makeReadyForSend();
        }catch (Exception ex){
            Logger.logException(ex);
        }
    }

    public static FCGIPostBody getInstance(byte[] body){
        if (body != null) return new FCGIPostBody(body);
        else return new FCGIPostBody(new byte[]{});
    }

    @Override
    protected void makeReadyForSend(){
        this.isReady = true;
    }
}
