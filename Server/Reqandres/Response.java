package Server.Reqandres;

import java.util.*;
import java.io.*;
import java.util.regex.*;
import java.net.URL;
import Server.Captcha.Captcha;
import Server.Captcha.Data;
import Server.Utils.*;
import Server.Reqandres.Senders.*;
import Server.Reqandres.CGI.CGIExecuter;

public class Response {

    private Request req;
    private HashMap headers;
    private String prot;
    private String ip;
    private String Host;
    private String path;
    private Methods Method;
    private URL url;
    private DataOutputStream out;
    private int id;


    public Response(Request rq,DataOutputStream ot,int ID){
        req = rq;
        id = ID;
        out = ot;
        this.headers = req.headers;
        ip = req.ip;
        prot = (String)headers.get("Version");
        url = (URL)headers.get("URL");
        Host = req.Host;
        path = req.Path;
        Method = (Methods) headers.get("Method");
        this.handleRes();
    }

    private void handleRes(){
        if(!headers.isEmpty()){
            if(req.sit < 300){
                switch (Method){
                    case CONNECT -> {
                        FileSender.setProt(prot);
                        FileSender.sendConnectMethod(out,ip,id,Host);
                    }
                    case OPTIONS -> {
                        FileSender.setProt(prot);
                        FileSender.sendOptionsMethod(out,ip,id,Host);
                    }
                    case PUT,DELETE -> {
                        FileSender.setProt(prot);
                        FileSender.setStatus(req.sit);
                        FileSender.send(null,out,ip,id,Host);
                    }
                    case TRACE -> {
                        FileSender.setProt(prot);
                        FileSender.setContentType("message/http");
                        FileSender.setStatus(200);
                        FileSender.sendFile(Method,req.tempFile,out,ip,id,Host);
                    }
                    default -> {
                        Logger.glog("Preparing response to " + ip + "  ; id = " + id, Host);
                        try {
                            switch (path){
                                case "/" -> {
                                    FileSender.setProt(prot);
                                    FileSender.setContentType("text/html");
                                    FileSender.setStatus(200);
                                    FileSender.sendFile(Method, new File(Configs.getMainDir(Host) + "/index.html"), out, ip, id, Host);
                                }
                                case "/getcp" -> {
                                    FileSender.setProt(prot);
                                    FileSender.setContentType("image/png");
                                    FileSender.setStatus(200);
                                    FileSender.sendFile(Method, new Captcha(ip,Host).image,out,ip,id,Host);
                                }
                                case "/chkcp" -> {
                                    FileSender.setProt(prot);
                                    FileSender.setStatus(200);
                                    FileSender.setContentType("text/plain");
                                    FileSender.send(Data.checkAnswer(ip,req.Body),out,ip,id,Host);
                                }
                                default -> {
                                    Pattern pt = Pattern.compile("\\.\\w+");
                                    Matcher mc = pt.matcher(path);
                                    String ext = "";
                                    if (mc.find()) ext = mc.group();
                                    File fl;
                                    String tempCntc = FileTypes.getContentType(ext);
                                    if (tempCntc != null) {
                                        if (tempCntc.equals("CGI") || tempCntc.contains("java")) {
                                            fl = new File(Configs.getCGIDir(Host) + path);
                                            String cmd = basicUtils.getExecCmd(ext.replace(".", ""));
                                            if (cmd != null && fl.exists()) {
                                                Logger.glog("CGI script requested. preparing for executing ...   ; id = " + id, Host);
                                                List<String> cmds = new ArrayList<>();
                                                cmds.add(cmd);
                                                cmds.add(fl.getAbsolutePath());
                                                new CGIExecuter(cmds, fl, id, Host, headers, url, out, req.Body, ip);
                                            } else {
                                                fl = new File(Configs.getMainDir(Host) + path);
                                                FileSender.setProt(prot);
                                                if (ext.equals(".js")) FileSender.setContentType(FileTypes.getContentType(".js"));
                                                else FileSender.setContentType("text/plain");
                                                FileSender.setStatus(200);
                                                FileSender.sendFile(Method, fl, out, ip, id, Host);
                                            }
                                        } else {
                                            fl = new File(Configs.getMainDir(Host) + path);
                                            if (fl.isFile()) {
                                                FileSender.setProt(prot);
                                                FileSender.setContentType(tempCntc);
                                                FileSender.setStatus(200);
                                                FileSender.sendFile(Method, fl, out, ip, id, Host);
                                            } else this.sendCode(404);
                                        }
                                    } else {
                                        fl = new File(Configs.getMainDir(Host) + path);
                                        if (fl.isFile()) {
                                            FileSender.setProt(prot);
                                            FileSender.setContentType(FileTypes.getContentType(".bin"));
                                            FileSender.setStatus(200);
                                            FileSender.sendFile(Method, fl, out, ip, id, Host);
                                        } else this.sendCode(404);
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            String t = "";
                            for (StackTraceElement a : ex.getStackTrace()) {
                                t += a.toString() + " ;; ";
                            }
                            t += ex.toString();
                            Logger.ilog(t);
                        }
                    }
                }
            }else this.sendCode(req.sit);
        }else{
            FileSender.setProt(prot);
            FileSender.setStatus(200);
            FileSender.setContentType("text/plain");
            FileSender.send(null,out,ip,id,Host);
        }
    }
    
    private void sendCode(int code){
        FileSender.setProt(prot);
        FileSender.setContentType("text/html");
        FileSender.setStatus(code);
        FileSender.sendFile(Method,new File(Configs.getCWD() + "/default_pages/" + code + ".html"),out,ip,id,Host);
    }

}