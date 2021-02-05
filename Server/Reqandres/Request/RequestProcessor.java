package Server.Reqandres.Request;

import Engines.DDOS.Interface;
import Server.HttpListener;
import Server.Method.Factory;
import Server.Reqandres.Proxy;
import Server.Utils.*;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.*;


public class RequestProcessor {

    private final Request req;
    public Methods method;
    public ArrayList<Byte> Body = new ArrayList();
    public int sit = 200;
    public int stat = 1;
    public boolean KA = false;

    public RequestProcessor(Request rq){
        this.req = rq;
        this.startProcessing();
    }

    private void startProcessing(){
        this.processRequest();
        if (KA)
            new HttpListener(req.getSock());
        if (stat != 0)
            this.continueProcess();
        else
            req.getCacheFile().delete();
    }

    private void continueProcess(){
        try{
            if (req.getCacheFile().length() > 5) {
                Interface.addReqVol(req.getIP(), req.getCacheFile().length());
                if (this.sit < 400) {
                    this.stat = Factory.getMt(this.method).run(req, this);
                } else {
                    basicUtils.sendCode(this.sit, req);
                    this.stat = 0;
                    req.getCacheFile().delete();
                }
            } else {
                req.out.flush();
                req.getCacheFile().delete();
                this.stat = 0;
            }
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    private String readLine(InputStream in){
        StringBuilder sb = new StringBuilder();
        int i;
        try{
            i = in.read();
            if (i == -1) return null;
            while (i != 13){
                if (i != 10) sb.append((char)i);
                i = in.read();
                if (i == -1) break;
            }
        }catch(Exception ex){
            Logger.logException(ex);
        }
        return sb.toString();
    }

    private void processRequest(){
        try{
            String line = this.readLine(req.is);
            if (line != null && line.length() > 5){
                String path = "";
                Pattern pathPattern = Pattern.compile(" /[^ ]*");
                Matcher pathMatcher = pathPattern.matcher(line);
                Pattern protPattern = Pattern.compile("HTTP/\\d[.]?\\d?");
                Matcher protMatcher = protPattern.matcher(line);
                if (pathMatcher.find() && protMatcher.find()){
                    path = URLDecoder.decode(pathMatcher.group().trim(), StandardCharsets.UTF_8);
                    req.Path = path;
                    req.setProt(protMatcher.group());
                    StringBuilder sb = new StringBuilder();
                    sb.append(line);
                    boolean hostFound = false;
                    while (true) {
                        line = this.readLine(req.is);
                        if (line != null) {
                            if (!line.startsWith("Host"))
                                sb.append("\r\n").append(line);
                            else {
                                hostFound = true;
                                break;
                            }
                        } else
                            break;
                    }
                    sb.append("\r\n").append(line).append("\r\n");
                    if (hostFound) {
                        String hostName = line.split(":", 2)[1].trim()
                                .replace("www.","");
                        int status = Configs.getHostStatus(hostName);
                        if (status == 0) {
                            String[] api = APIConfigs.getAPIAddress(hostName + path);
                            if (api == null) {
                                req.setHost(hostName);
                                this.readRequest(sb.toString());
                                if (this.method == Methods.POST && this.sit == 200) {
                                    this.parseBody();
                                }
                            } else {
                                if (api.length > 1) {
                                    req.getSock().setSoTimeout(0);
                                    req.setHost(hostName);
                                    Logger.glog("request for API " + hostName + path + " received from " + req.getIP() + " .", hostName);
                                    new Proxy(api, sb.substring(0, sb.length() - 2), req);
                                    this.stat = 0;
                                } else {
                                    req.setHost(hostName);
                                    req.Path = api[0];
                                    this.readRequest(sb.toString());
                                }
                            }
                        } else if (status == 1) {
                            req.getSock().setSoTimeout(0);
                            req.setHost(hostName);
                            Logger.glog("request for " + hostName + " received from " + req.getIP() + " .", hostName);
                            new Proxy(Configs.getForwardAddress(hostName), sb.substring(0, sb.length() - 2), req);
                            this.stat = 0;
                        } else if (status == 2) {
                            basicUtils.redirect(307, Configs.getForwardAddress(hostName)[0], req);
                            this.stat = 0;
                        } else {
                            req.setHost(hostName);
                            this.readRequest(sb.toString());
                        }
                    } else
                        this.sit = 400;
                }else
                    this.sit = 400;
            }else{
                req.out.flush();
                this.stat = 0;
                KA = false;
            }
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    private void authenticate(){
        if (Server.HttpAuth.Interface.needAuth(req.getHost() + req.Path)){
            this.sit = Server.HttpAuth.Interface.evaluate(req.getHeaders(),req.getIP());
            if (this.sit == 401){
                this.stat = 0;
                Server.HttpAuth.Interface.send401(req);
            }
        }
    }

    private void determineKeepAlive(){
        Object cnc = req.getHeaders().get("Connection");
        if (cnc != null) {
            String con = (String) cnc;
            KA = Configs.keepAlive && !con.trim().equals("close");
        } else KA = false;
    }

    private void fixTheHeaders(){
        try{
            if (this.method != Methods.CONNECT && this.method != Methods.OPTIONS) {
                String prt = (String) req.getHeaders().get("Version");
                String url = prt.split("/")[0] + "://" + req.getHost() + req.Path;
                URL u = new URL(url);
                req.getHeaders().replace("URL", u);
                req.setURL(u);
                Logger.glog(req.getIP() + "'s request is for ' " + u.getPath() + " '    ; id = " + req.getID(), req.getHost());
            }
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    private void readRequest(String redReq){
        try(FileOutputStream fw = new FileOutputStream(req.getCacheFile(),true)){
            String[] currentRead = redReq.split("\n");
            fw.write(redReq.getBytes());
            String[] p = currentRead[0].split(" ", 3);
            this.method = switch (p[0]) {
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
                req.setProt(p[2].trim());
                req.getHeaders().put("Method", this.method);
                req.getHeaders().put("URL", p[1]);
                req.getHeaders().put("Version", p[2]);
                req.orgPath = URLDecoder.decode(p[1],StandardCharsets.UTF_8);
                if (req.Path == null)
                    req.Path = req.orgPath;
                for (int i = 1; i < currentRead.length; i++) {
                    String[] tmp = currentRead[i].split(":", 2);
                    if (tmp.length != 1) req.getHeaders().put(tmp[0].trim(), tmp[1].trim());
                }
                String line;
                while ((line = this.readLine(req.is)) != null) {
                    if (!line.isEmpty()) {
                        fw.write((line + "\r\n").getBytes());
                        String[] tmp = line.split(":", 2);
                        if (tmp.length != 1) req.getHeaders().put(tmp[0].trim(), tmp[1].trim());
                    } else break;
                }
                fw.write((line + "\r\n").getBytes());
                this.determineKeepAlive();
                this.authenticate();
                if (this.sit < 300) {
                    this.fixTheHeaders();
                    if (this.method == Methods.POST || this.method == Methods.PUT) {
                        if (req.getHeaders().get("Content-Length") != null) {
                            int length = Integer.parseInt((String) req.getHeaders().get("Content-Length"));
                            if (length < Configs.postBodySize) {
                                try {
                                    //Reads the body
                                    fw.write(req.is.readNBytes(length + 1));
                                } catch (Exception ex) {
                                    Logger.logException(ex);
                                }
                            } else this.sit = 413;
                        } else this.sit = 411;
                    }
                }
            } else this.sit = 400;
            fw.flush();
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    public void parseBody(){
        try(RandomAccessFile bf = new RandomAccessFile(req.getCacheFile(), "r")){
            Pattern ptn = Pattern.compile("boundary=[^\n]+");
            Matcher mc = ptn.matcher((String)req.getHeaders().get("Content-Type"));
            String bnd ;
            String line;
            while(!bf.readLine().isEmpty()){}
            ArrayList<String[]> files = new ArrayList<>();
            if (mc.find()) {
                bnd = "--" + mc.group().replace("boundary=", "");
                while((line = bf.readLine()) != null){
                    if(line.startsWith(bnd)){
                        String detailLine = bf.readLine();
                        if (detailLine == null) break;
                        ptn = Pattern.compile("filename=\"[^\"]+");
                        mc = ptn.matcher(detailLine);
                        Pattern nameptn = Pattern.compile("name=\"[^\"]+");
                        Matcher namemc = nameptn.matcher(detailLine);
                        if(mc.find()){
                            String fileName = mc.group().replace("filename=","").replace("\"","");
                            if (fileName.isEmpty()) continue;
                            String filead = Configs.getUploadDir((String)this.req.getHeaders().get("Host")) + "/" + fileName;
                            if(namemc.find()) this.addToCGIBody((namemc.group().replace("name=","").replace("\"","") + "=" + filead).getBytes());
                            String[] file = new String[3];
                            file[0] = filead;
                            bf.readLine();
                            bf.readLine();
                            long on = bf.getFilePointer();
                            file[1] = String.valueOf(on);
                            long off;
                            while(true){
                                off = bf.getFilePointer();
                                line = bf.readLine();
                                if (line.startsWith(bnd)){
                                    file[2] = String.valueOf(off);
                                    bf.seek(off);
                                    break;
                                }
                            }
                            if (off - on < Configs.fileSize) files.add(file);
                        }else if (namemc.find()){
                            bf.readLine();
                            String val = bf.readLine();
                            if(!val.isEmpty()) this.addToCGIBody((namemc.group().replace("name=\"","") + "=" + val).getBytes());
                        }
                    }
                }
            }else {
                bf.read();
                byte[] temp = new byte[(int)(bf.length() - bf.getFilePointer())];
                bf.read(temp);
                this.addToCGIBody(temp);
            }
            bf.close();
            if (!files.isEmpty()) new FileFixer(files,req.getCacheFile());
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }

    private void addToCGIBody(byte[] data){
        if(Body.isEmpty()) for (byte b : data) Body.add(b);
        else{
            Body.add((byte)'&');
            for (byte b : data) Body.add(b);
        }
    }
}