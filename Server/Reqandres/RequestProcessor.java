package Server.Reqandres;

import Engines.DDOS.Interface;
import Server.HttpListener;
import Server.Method.Factory;
import Server.Utils.*;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;


public class RequestProcessor {

    public RandomAccessFile bf;
    private final Request req;
    public Methods method;
    public ArrayList<Byte> Body = new ArrayList();
    public final HashMap headers = new HashMap();
    public int sit = 0;
    public int stat = 1;
    public boolean KA;

    public RequestProcessor(Request rq){
        this.req = rq;
        this.processRequest();
        req.setHeaders(this.headers);
        if (Server.HttpAuth.Interface.needAuth(req.getHost() + req.Path)){
            this.sit = Server.HttpAuth.Interface.evaluate(this.headers,req.getIP());
            if (this.sit == 401){
                this.stat = 0;
                Server.HttpAuth.Interface.send401(req);
            }
        }
        this.continueProcess();
    }

    private void continueProcess(){
        try{
            if (Configs.keepAlive && KA)
                new HttpListener(req.getSock());
            if (this.stat != 0){
                if (bf.length() > 5) {
                    Interface.addReqVol(req.getIP(), bf.length());
                    if (this.sit < 400) {
                        this.stat = Factory.getMt(this.method).run(req, this);
                    } else {
                        basicUtils.sendCode(this.sit, req);
                        this.stat = 0;
                        req.getCacheFile().delete();
                    }
                    bf.close();
                } else {
                    req.out.flush();
                    req.out.close();
                    bf.close();
                    req.getCacheFile().delete();
                    this.stat = 0;
                }
            }else req.getCacheFile().delete();
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
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
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
        return sb.toString();
    }

    private void processRequest(){
        try{
            InputStream reader  = req.is;
            String line = this.readLine(reader);
            if (line != null){
                String path = "";
                Pattern pathPattern = Pattern.compile(" /[^ ]*");
                Matcher pathMatcher = pathPattern.matcher(line);
                if (pathMatcher.find())
                    path = pathMatcher.group().trim();
                StringBuilder sb = new StringBuilder();
                sb.append(line);
                while(!(line = this.readLine(reader)).startsWith("Host"))
                    sb.append("\r\n").append(line);
                sb.append("\r\n").append(line).append("\r\n");
                String hostName = line.split(":",2)[1].trim();
                int status = Configs.getHostStatus(hostName);
                System.out.println(status);
                if (status == 0){
                    String[] api = APIConfigs.getAPIAddress(hostName + path);
                    if (api == null) {
                        this.readRequest(sb.toString());
                        bf = new RandomAccessFile(req.getCacheFile(), "r");
                        this.sit = Perms.isDirPerm(Configs.getMainDir(hostName) + path);
                        if (this.method == Methods.POST && this.sit == 200) {
                            this.parseBody();
                        }else bf.close();
                    } else {
                        if (api.length > 1) {
                            Logger.glog("request for API " + hostName + path + " received from " + req.getIP() + " .", hostName);
                            new SubForwarder(api,sb.substring(0,sb.length()-2),req);
                            this.stat = 0;
                        } else
                            req.Path = api[0];
                    }
                }
                else if (status == 1){
                    Logger.glog("request for " + hostName + " received from " + req.getIP() + " .", hostName);
                    new SubForwarder(Configs.getForwardAddress(hostName),sb.substring(0,sb.length()-2),req);
                    req.getSock().close();
                    this.stat = 0;
                }
                else if (status == 2){
                    basicUtils.redirect(307,Configs.getForwardAddress(hostName)[0],req);
                    this.stat = 0;
                }
                else
                    this.readRequest(sb.toString());
            }else{
                req.out.flush();
                this.stat = 0;
            }
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    private void readRequest(String redReq){
        try(FileOutputStream fw = new FileOutputStream(req.getCacheFile(),true)){
            InputStream reader = req.is;
            String[] currentRead = redReq.split("\n");
            fw.write(redReq.getBytes());
            Pattern vr = Pattern.compile("HTTP/\\d.\\d");
            Matcher mct = vr.matcher(currentRead[0]);
            if (mct.find()) {
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
                    req.setProt(p[2]);
                    headers.put("Method", this.method);
                    headers.put("URL", p[1]);
                    headers.put("Version", p[2]);
                    for (int i = 1 ; i < currentRead.length ; i++){
                        String[] tmp = currentRead[i].split(":", 2);
                        if (tmp.length != 1) headers.put(tmp[0].trim(), tmp[1].trim());
                    }
                    String line;
                    while ((line = this.readLine(reader)) != null) {
                        if (!line.isEmpty()) {
                            fw.write((line + "\r\n").getBytes());
                            String[] tmp = line.split(":", 2);
                            if (tmp.length != 1) headers.put(tmp[0].trim(), tmp[1].trim());
                        } else break;
                    }
                    fw.write((line + "\r\n").getBytes());
                    if (this.method != Methods.CONNECT && this.method != Methods.OPTIONS) {
                        String prt = (String) headers.get("Version");
                        String url = prt.split("/")[0] + "://" + headers.get("Host") + URLDecoder.decode((String) headers.get("URL"), StandardCharsets.UTF_8);
                        URL u = new URL(url);
                        String Host = u.getHost().replace("www.", "").replace("ww2.", "");
                        if (u.getPort() != -1) Host += ":" + u.getPort();
                        req.setHost(Host);
                        headers.replace("URL", u);
                        req.setURL(u);
                        if (req.Path == null)
                            req.Path = u.getPath();
                        req.orgPath = u.getPath();
                        Logger.glog(req.getIP() + "'s request is for " + u.getPath() + "; id = " + req.getID(),req.getHost());
                        Object cnc = headers.get("Connection");
                        if (cnc != null){
                            String con = (String) cnc;
                            KA = !con.trim().equals("close");
                        }else KA = false;
                    }
                    if (this.method == Methods.POST || this.method == Methods.PUT) {
                        if (headers.get("Content-Length") != null) {
                            int length = Integer.parseInt((String) headers.get("Content-Length"));
                            if (length < Configs.postBodySize) {
                                try {
                                    fw.write(reader.readNBytes(length + 1));
                                } catch (Exception ex) {
                                    String t = "";
                                    for (StackTraceElement a : ex.getStackTrace()) {
                                        t += a.toString() + " ;; ";
                                    }
                                    t += ex.toString();
                                    Logger.ilog(t);
                                }
                            } else this.sit = 413;
                        } else this.sit = 411;
                    }
                } else this.sit = 400;
            } else this.sit = 400;
            fw.flush();
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    public void parseBody(){
        try{
            Pattern ptn = Pattern.compile("boundary=[^\n]+");
            Matcher mc = ptn.matcher((String)headers.get("Content-Type"));
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
                            String filead = Configs.getUploadDir((String)this.headers.get("Host")) + "/" + fileName;
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
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
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