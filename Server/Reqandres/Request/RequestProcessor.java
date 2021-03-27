package Server.Reqandres.Request;

import Engines.DDOS.Interface;
import Server.HttpListener;
import Server.Method.Factory;
import Server.Utils.Configs.APIConfigs;
import Server.Utils.Configs.Configs;
import Server.Utils.Configs.ProxyConfigs;
import Server.Reqandres.Senders.QuickSender;
import Server.Utils.Reader.BodyParser;
import Server.Utils.Proxy;
import Server.Utils.*;
import Server.Utils.Reader.RequestReader;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.*;

public class RequestProcessor {

    private final Request req;
    private final RequestReader rp;
    public Methods method;
    public ArrayList<Byte> Body = new ArrayList<>();
    public int sit = 200;
    public int stat = 1;
    public boolean KA = false;

    public RequestProcessor(Request rq){
        this.req = rq;
        this.rp = new RequestReader(req);
        this.startProcessing();
    }

    private void startProcessing(){
        if (ProxyConfigs.isOn){
            Logger.glog("Proxy is on, request is being forwarded.",req.getHost());
            new Proxy(ProxyConfigs.getAddress(),false,req);
            this.stat = 0;
        }else{
            rp.readHeaders();
            this.processRequest();
            if (KA)
                new HttpListener(req.getSocket(),req.getHost(),false);
            if (stat != 0)
                this.continueProcess();
            else
                req.getCacheFile().delete();
        }
    }

    private void continueProcess(){
        try{
            if (req.getCacheFile().length() > 5) {
                Interface.addReqVol(req.getIP(), req.getCacheFile().length());
                if (this.sit < 400) {
                    this.stat = Factory.getMt(this.method).run(req, this);
                } else {
                    new QuickSender(this.req).sendCode(this.sit);
                    this.stat = 0;
                    req.getCacheFile().delete();
                }
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
            String line = rp.getFirstLine();
            if (!req.getHeaders().isEmpty()){
                Pattern pathPattern = Pattern.compile(" /[^ ]*");
                Matcher pathMatcher = pathPattern.matcher(line);
                Pattern protPattern = Pattern.compile("HTTP/\\d[.]?\\d?");
                Matcher protMatcher = protPattern.matcher(line);
                if (pathMatcher.find() && protMatcher.find()){
                    req.setOrgPath(pathMatcher.group());
                    req.setPath(req.getOrgPath());
                    Matcher mch = Pattern.compile("/[^?]+").matcher(req.getOrgPath());
                    if (mch.find())
                        req.setPath(mch.group());
                    req.setProt(protMatcher.group());
                    String host = req.getHeaders().get("Host");
                    if (host != null) {
                        String hostName = URLDecoder.decode(host.trim().replace("www.",""),StandardCharsets.UTF_8);
                        req.setHost(hostName);
                        int status = Configs.getHostStatus(hostName);
                        if (status == 0) {
                            String[] api = APIConfigs.getAPIAddress(hostName + req.getPath(),hostName);
                            if (api == null) {
                                this.readRequest();
                                if (this.method == Methods.POST && this.sit == 200)
                                    new BodyParser(this.req).parseBody();
                            } else {
                                if (api.length > 1) {
                                    req.setTimeout(0);
                                    rp.finishReading();
                                    Logger.glog("request for API " + hostName + req.getPath() + " received from " + req.getIP() + " .", hostName);
                                    new Proxy(api, true, req);
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
                            new Proxy(Configs.getForwardAddress(hostName), true, req);
                            this.stat = 0;
                        } else if (status == 2) {
                            basicUtils.redirect(307, Configs.getForwardAddress(hostName)[0], req);
                            this.stat = 0;
                        } else {
                            this.readRequest();
                        }
                    } else{
                        this.stat = 0;
                        new QuickSender(req).sendBadReq("\"Host\" header not found.");
                        if (Configs.BRS)
                            Interface.addWarning(req.getIP(),req.getHost());
                    }
                }else{
                    this.stat = 0;
                    new QuickSender(req).sendBadReq("Invalid headers.");
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
        if (Server.HttpAuth.Interface.getInstance().needAuth(req.getHost() + req.getPath(),req.getHost())){
            this.sit = Server.HttpAuth.Interface.getInstance().evaluate(req.getHeaders(),req.getIP(), req.getHost());
            if (this.sit == 401){
                this.stat = 0;
                Server.HttpAuth.Interface.getInstance().send401(req);
            }
        }
    }

    private void determineKeepAlive(){
        String cnc = req.getHeaders().get("Connection");
        if (cnc != null) {
            KA = Configs.getKeepAlive(req.getHost()) && cnc.trim().equals("keep-alive");
        } else KA = false;
        req.setKeepAlive(Configs.getKeepAlive(req.getHost()) && this.KA);
    }

    private void fixTheHeaders(){
        try{
            if (this.method != Methods.CONNECT && this.method != Methods.OPTIONS) {
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
            this.method = switch (rp.getFirstLine().split(" ", 3)[0]) {
                case "GET" -> Methods.GET;
                case "POST" -> Methods.POST;
                case "PUT" -> Methods.PUT;
                case "HEAD" -> Methods.HEAD;
                case "DELETE" -> Methods.DELETE;
                case "CONNECT" -> Methods.CONNECT;
                case "OPTIONS" -> Methods.OPTIONS;
                case "TRACE" -> Methods.TRACE;
                default -> Methods.UNKNOWN;
            };
            req.setMethod(this.method);
            if (this.method != Methods.UNKNOWN) {
                this.determineKeepAlive();
                this.authenticate();
                if (this.sit < 300) {
                    this.fixTheHeaders();
                    if (this.method == Methods.POST || this.method == Methods.PUT) {
                        if (rp.hasBody()) {
                            if (rp.getBodyLength() != -1){
                                if (rp.getBodyLength() < Configs.getPostBodySize(req.getHost())) {
                                    rp.readBody();
                                } else
                                    this.sit = 413;
                            } else
                                this.sit = 411;
                        }
                    }
                }
            } else{
                this.stat = 0;
                new QuickSender(req).sendBadReq("Invalid method.");
                Interface.addWarning(req.getIP(),req.getHost());
            }
            rp.finishReading();
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }
}