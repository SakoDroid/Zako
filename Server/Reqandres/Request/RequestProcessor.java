package Server.Reqandres.Request;

import Engines.DDOS.Interface;
import Server.HttpListener;
import Server.Reqandres.Responses.Factory;
import Server.Reqandres.HeaderCheck.HeadersChecker;
import Server.Utils.Configs.APIConfigs;
import Server.Utils.Configs.Configs;
import Server.Utils.Configs.HTAccess;
import Server.Utils.Configs.ProxyConfigs;
import Server.Reqandres.Senders.QuickSender;
import Server.Utils.Enums.Methods;
import Server.Utils.Reader.BodyParser;
import Server.Utils.Proxy;
import Server.Utils.*;
import Server.Utils.Reader.RequestReader;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class RequestProcessor {

    private final Request req;
    private final RequestReader rp;
    public int stat = 1;

    public RequestProcessor(Request rq){
        this.req = rq;
        this.rp = new RequestReader(req);
        this.startProcessing();
    }

    private void startProcessing(){
        rp.readHeaders();
        this.processRequest();
        if (req.getKeepAlive() && !ProxyConfigs.isOn)
            new HttpListener(req.getSocket(),req.getHost(),false);
        if (stat != 0)
            this.continueProcess();
        else
            req.getCacheFile().delete();

    }

    private void continueProcess(){
        try{
            if (req.getCacheFile().length() > 5) {
                Interface.addReqVol(req.getIP(), req.getCacheFile().length());
                new HeadersChecker(req);
                new Factory().getResponse(req.getResponseCode()).init(req);
            } else {
                req.getCacheFile().delete();
                this.stat = 0;
            }
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    private void processRequest(){
        try{
            if (!req.getHeaders().isEmpty()){
                if (rp.isPathFound()){
                    if (rp.isProtoFound()){
                        if (this.isProtoValid(req.getProt())) {
                            if (rp.isHostFound()) {
                                if (req.getMethod() != Methods.UNKNOWN) {
                                    if (ProxyConfigs.isOn) {
                                        rp.finishReading();
                                        Logger.glog("Proxy is on, request is being forwarded.", req.getHost());
                                        new Proxy(ProxyConfigs.getAddress(), req);
                                        this.stat = 0;
                                    } else {
                                        String hostName = URLDecoder.decode(req.getHost().trim().replace("www.", ""), StandardCharsets.UTF_8);
                                        int status = Configs.getHostStatus(hostName);
                                        if (status == 0) {
                                            String[] api = APIConfigs.getAPIAddress(hostName + req.getPath(), hostName);
                                            if (api == null) {
                                                this.readRequest();
                                                if (req.getMethod() == Methods.POST && req.getResponseCode() == 200)
                                                    new BodyParser(this.req).parseBody();
                                            } else {
                                                if (api.length > 1) {
                                                    req.setTimeout(0);
                                                    rp.finishReading();
                                                    Logger.glog("request for API " + hostName + req.getPath() + " received from " + req.getIP() + " .", hostName);
                                                    new Proxy(api, req);
                                                    this.stat = 0;
                                                } else {
                                                    req.setPath(api[0]);
                                                    this.readRequest();
                                                }
                                            }
                                        } else if (status == 1) {
                                            req.setTimeout(0);
                                            rp.finishReading();
                                            Logger.glog("request for " + hostName + " received from " + req.getIP() + " .", hostName);
                                            new Proxy(Configs.getForwardAddress(hostName), req);
                                            this.stat = 0;
                                        } else if (status == 2) {
                                            basicUtils.redirect(307, Configs.getForwardAddress(hostName)[0], req);
                                            this.stat = 0;
                                        } else {
                                            this.readRequest();
                                        }
                                    }
                                } else {
                                    this.stat = 0;
                                    new QuickSender(req).sendBadReq("Invalid method.");
                                    Interface.addWarning(req.getIP(), req.getHost());
                                }
                            } else {
                                this.stat = 0;
                                new QuickSender(req).sendBadReq("\"Host\" header not found.");
                                if (Configs.BRS)
                                    Interface.addWarning(req.getIP(), req.getHost());
                            }
                        }else{
                            this.stat = 0;
                            new QuickSender(req).sendCode(505);
                        }
                    }else{
                        this.stat = 0;
                        new QuickSender(req).sendBadReq("No protocol found.");
                        if (Configs.BRS)
                            Interface.addWarning(req.getIP(),req.getHost());
                    }
                }else{
                    this.stat = 0;
                    new QuickSender(req).sendBadReq("No path found.");
                    if (Configs.BRS)
                        Interface.addWarning(req.getIP(),req.getHost());
                }
            }else{
                this.stat = 0;
                new QuickSender(req).sendBadReq("Empty request");
                if (Configs.BRS)
                    Interface.addWarning(req.getIP(),req.getHost());
            }
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    private void authenticate(){
        if (Server.HttpAuth.Interface.getInstance().needAuth(req.getHost() + req.getPath(),req.getHost()))
            req.setResponseCode(Server.HttpAuth.Interface.getInstance().evaluate(req.getHeaders(),req.getIP(), req.getHost()));
    }

    private void determineKeepAlive(){
        String cnc = req.getHeaders().get("Connection");
        if (cnc != null) {
            req.setKeepAlive(HTAccess.getInstance().isKeepAliveAllowed(req.getHost()) && cnc.trim().equals("keep-alive"));
        }
    }

    private void fixTheHeaders(){
        try{
            if (req.getMethod() != Methods.CONNECT && req.getMethod() != Methods.OPTIONS) {
                String prt = req.getProt();
                String url = prt.split("/")[0] + "://" + req.getHost() + req.getOrgPath();
                req.setURL(new URL(url));
                Logger.glog(req.getIP() + " is requesting " + req.getPath() + "   ;; debug_id = " + req.getID(), req.getHost());
            }
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    private void readRequest(){
        try{
            this.determineKeepAlive();
            this.authenticate();
            if (req.getResponseCode() < 300) {
                this.fixTheHeaders();
                if (req.getMethod() == Methods.POST || req.getMethod() == Methods.PUT) {
                    if (rp.hasBody()) {
                        if (rp.getBodyLength() != -1){
                            if (rp.getBodyLength() < Configs.getPostBodySize(req.getHost())) {
                                rp.readBody();
                            } else
                                req.setResponseCode(413);
                        } else
                           req.setResponseCode(411);
                    }
                }
            }
            rp.finishReading();
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    private boolean isProtoValid(String proto){
        if (proto.startsWith("HTTP/")){
            if (proto.equals("HTTP/1.1"))
                return true;
            else
                return false;
        }
        return false;
    }
}