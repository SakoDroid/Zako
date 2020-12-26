package Server.Reqandres;

import Server.DDOS.Interface;
import Server.Reqandres.Senders.FileSender;
import Server.Utils.*;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;


public class RequestProcessor {

    private RandomAccessFile bf;
    private final Request req;
    private Methods method;
    public String Body = "";
    public String ip;
    public final HashMap headers = new HashMap();
    private int id;
    public String Host;
    public String Path;
    public int sit = 0;
    public int stat = 1;

    public RequestProcessor(Request rq){
        this.req = rq;
        this.id = rq.getID();
        this.ip = rq.getIP();
        try{
            this.read();
            bf = new RandomAccessFile(rq.getCacheFile(),"r");
            if (bf.length() > 10){
                Interface.addReqVol(ip,bf.length());
                if (this.sit < 300){
                    switch (this.method) {
                        case CONNECT -> this.sit = 200;
                        case PUT -> this.handlePUT();
                        case DELETE -> this.handleDELETE();
                        case OPTIONS -> this.handleOptions(rq.out);
                        case TRACE -> this.handleTrace(rq.out);
                        default -> {
                            if (!basicUtils.LocalHostIP.isEmpty())
                                Host = Host.replace(basicUtils.LocalHostIP, Configs.MainHost);
                            Host = Host.replace("127.0.0.1", "localhost");
                            int status = Configs.getHostStatus(Host);
                            if (status == 0) {
                                String[] api = APIConfigs.getAPIAddress(Host + Path);
                                if (api == null) {
                                    sit = Perms.isDirPerm(Configs.getMainDir(Host) + Path);
                                    if (this.method == Methods.POST) {
                                        parseBody();
                                    } else bf.close();
                                } else {
                                    if (api.length > 1) {
                                        stat = 0;
                                        bf.close();
                                        Logger.glog("request for API " + Host + Path + " received from " + ip + " .", Host);
                                        new SubForwarder(api, rq.getCacheFile(), rq.out, ip, Host + Path);
                                    } else {
                                        Path = api[0];
                                    }
                                }
                            } else if (status == 1) {
                                stat = 0;
                                bf.close();
                                Logger.glog("request for " + Host + " received from " + ip + " .", Host);
                                new SubForwarder(Configs.getForwardAddress(Host), rq.getCacheFile(), rq.out, ip, Host);
                            } else sit = 500;
                        }
                    }
                }
                bf.close();
            }else {
                rq.out.flush();
                rq.out.close();
                bf.close();
                rq.getCacheFile().delete();
                this.stat = 0;
            }
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
                if (i != 10 ) sb.append((char)i);
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

    private void read(){
        try{
            FileOutputStream fw = new FileOutputStream(req.getCacheFile(),true);
            InputStream reader  = req.is;
            String line = this.readLine(reader);
            fw.write((line + "\r\n").getBytes());
            Pattern vr = Pattern.compile("HTTP/\\d.\\d");
            Matcher mct = vr.matcher(line);
            if (mct.find()){
                String[] p = line.split(" ", 3);
                this.method = switch (p[0]){
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
                if (this.method != Methods.UNKNOWN){
                    headers.put("Method", this.method);
                    headers.put("URL", p[1]);
                    headers.put("Version", p[2]);
                    while ((line = this.readLine(reader)) != null) {
                        if (!line.isEmpty()) {
                            fw.write((line + "\r\n").getBytes());
                            String[] tmp = line.split(":", 2);
                            if (tmp.length != 1) headers.put(tmp[0].trim(), tmp[1].trim());
                        }else break;
                    }
                    fw.write((line + "\r\n").getBytes());
                    if (this.method != Methods.CONNECT && this.method != Methods.OPTIONS) {
                        String prt = (String) headers.get("Version");
                        String url = prt.split("/")[0] + "://" + headers.get("Host") + URLDecoder.decode((String) headers.get("URL"), StandardCharsets.UTF_8);
                        URL u = new URL(url);
                        Host = u.getHost().replace("www.", "").replace("ww2.", "");
                        req.setHost(Host);
                        if (u.getPort() != -1) Host += ":" + u.getPort();
                        Path = u.getPath();
                        headers.replace("URL", u);
                    }
                    if (this.method == Methods.POST || this.method == Methods.PUT){
                        if (headers.get("Content-Length") != null){
                            int length = Integer.parseInt((String)headers.get("Content-Length"));
                            if (length < Configs.postBodySize){
                                try{

                                    //Uses less ram but it is slower
                                    /*for (int l = 0 ; l < length ; ++l){
                                        fw.write(reader.read());
                                    }*/

                                    //Uses more ram but it's faster.
                                    fw.write(reader.readNBytes(length+1));
                                }catch(Exception ex){
                                    String t = "";
                                    for (StackTraceElement a : ex.getStackTrace()) {
                                        t += a.toString() + " ;; ";
                                    }
                                    t += ex.toString();
                                    Logger.ilog(t);
                                }
                            }else this.sit = 413;
                        }else this.sit = 411;
                    }
                }else this.sit = 400;
            }else this.stat = 400;
            fw.flush();
            fw.close();
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    private void handleDELETE(){
        try{
            if(Perms.isIPAllowedForPUTAndDelete(ip)){
                File fl = new File(Configs.getMainDir(Host) + Path);
                if (fl.exists()) {
                    fl.delete();
                    this.sit = 200;
                } else this.sit = 404;
            }else this.sit = 405;
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    private void handleOptions(DataOutputStream out){
        this.stat = 0;
        FileSender.setProt((String) headers.get("Version"));
        FileSender.sendOptionsMethod(out,ip,id,Host);
    }

    private void handleTrace(DataOutputStream out){
        this.stat = 0;
        FileSender.setProt((String) headers.get("Version"));
        FileSender.setContentType("message/http");
        FileSender.setStatus(200);
        FileSender.sendFile(this.method,req.getCacheFile(),req.out,ip,id,Host);
    }

    private void handlePUT(){
        try{
            if(Perms.isIPAllowedForPUTAndDelete(ip)){
                File fl = new File(Configs.getMainDir(Host) + Path);
                FileOutputStream fos = new FileOutputStream(fl);
                int i;
                while ((i = bf.read()) != -1) {
                    fos.write(i);
                }
                fos.flush();
                fos.close();
                bf.close();
                this.sit = 201;
            }else this.sit = 405;
        }catch(Exception ex){
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }

    private void parseBody(){
        try{
            Pattern ptn = Pattern.compile("boundary=[^\n]+");
            Matcher mc = ptn.matcher((String)headers.get("Content-Type"));
            String bnd ;
            String line;
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
                            if(namemc.find()) this.addToCGIBody(namemc.group().replace("name=","").replace("\"","") + "=" + filead);
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
                            if(!val.isEmpty()) this.addToCGIBody(namemc.group() + "=" + bf.readLine());
                        }
                    }
                }
            }else {
                while((line = bf.readLine()) != null){
                    Body += line;
                }
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

    private void addToCGIBody(String data){
        if(Body.isEmpty()) Body += data;
        else Body += "&" + data;
    }
}