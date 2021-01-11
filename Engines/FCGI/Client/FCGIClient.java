package Engines.FCGI.Client;

import Engines.FCGI.Client.Core.FCGIEngine;
import Engines.FCGI.Client.Request.*;
import Engines.FCGI.Client.Response.FCGIResponse;
import Engines.FCGI.Client.Utils.*;

import java.util.Arrays;
import java.util.Map;

public class FCGIClient {

    public final int reqID;
    private final Map<String,String> envs;
    private final byte[] postBody;
    private final FCGIEngine engine;

    public FCGIClient(Map<String,String> envs, byte[] postBody,String ext){
        this.reqID = Utils.getID();
        this.envs = envs;
        this.postBody = postBody;
        String[] server = Configs.getServer(ext);
        this.engine = new FCGIEngine(server[0],Integer.parseInt(server[1]),Configs.timeOut);
    }

    public void run(){


        FCGIBeginRequestBody brb = FCGIBeginRequestBody.getInstance(FCGIConstants.FCGI_ROLE_RESPONSER,0);
        FCGIRequestHeader brbh = new FCGIRequestHeader(1,FCGIConstants.FCGI_BEGIN_REQUEST,reqID,brb.getLength());
        engine.exec(new FCGIRequest(brbh,brb));

        FCGIParamsBody params = FCGIParamsBody.getInstance(envs);
        FCGIRequestHeader prmh = new FCGIRequestHeader(1,FCGIConstants.FCGI_PARAMS,reqID, params.getLength());
        engine.exec(new FCGIRequest(prmh,params));

        FCGIParamsBody endParams = FCGIParamsBody.getInstance(null);
        FCGIRequestHeader endParamsH = new FCGIRequestHeader(1,FCGIConstants.FCGI_PARAMS,reqID, endParams.getLength());
        engine.exec(new FCGIRequest(endParamsH,endParams));

        if (postBody != null && postBody.length > 0){
            if (postBody.length < 65535){
                FCGIPostBody post = FCGIPostBody.getInstance(postBody);
                FCGIRequestHeader postH = new FCGIRequestHeader(1,FCGIConstants.FCGI_STDIN,reqID,post.getLength());
                engine.exec(new FCGIRequest(postH,post));
            }
            else {
                int parts = (int)Math.ceil((float)postBody.length / 65535);
                int i;
                for (i = 0 ; i < parts-1 ; i++){
                    FCGIPostBody post = FCGIPostBody.getInstance(Arrays.copyOfRange(postBody,i*65535,(i+1)*65535));
                    FCGIRequestHeader postH = new FCGIRequestHeader(1,FCGIConstants.FCGI_STDIN,reqID,post.getLength());
                    engine.exec(new FCGIRequest(postH,post));
                }
                FCGIPostBody post = FCGIPostBody.getInstance(Arrays.copyOfRange(postBody,i*65535,postBody.length));
                FCGIRequestHeader postH = new FCGIRequestHeader(1,FCGIConstants.FCGI_STDIN,reqID,post.getLength());
                engine.exec(new FCGIRequest(postH,post));
            }
            FCGIPostBody endPost = FCGIPostBody.getInstance(null);
            FCGIRequestHeader endPostH = new FCGIRequestHeader(1,FCGIConstants.FCGI_STDIN,reqID,endPost.getLength());
            engine.exec(new FCGIRequest(endPostH,endPost));
        }
    }

    public FCGIResponse getResponse(){
        return engine.listenForResponse(reqID);
    }
}
