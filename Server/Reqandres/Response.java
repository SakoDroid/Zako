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

    private final RequestProcessor reqes;
    private final Request request;
    private final HashMap headers;
    private final String prot;
    private final String Host;
    private final String path;
    private final Methods Method;
    private final URL url;


    public Response(RequestProcessor rq, Request req){
        this.reqes = rq;
        this.request = req;
        this.headers = rq.headers;
        prot = (String)headers.get("Version");
        url = (URL)headers.get("URL");
        Host = rq.Host;
        path = rq.Path;
        Method = (Methods) headers.get("Method");
        this.handleRes();
    }

    private void handleRes(){
        if(!headers.isEmpty()){
            if(reqes.sit < 300){
                switch (Method){
                    case CONNECT -> {
                        FileSender.setProt(prot);
                        FileSender.sendConnectMethod(request.out,request.getIP(),request.getID(),Host);
                    }
                    case PUT,DELETE -> {
                        FileSender.setProt(prot);
                        FileSender.setStatus(reqes.sit);
                        FileSender.send(null,request.out,request.getIP(),request.getID(),Host);
                    }
                    default -> {
                        Logger.glog("Preparing response to " + request.getIP() + "  ; id = " + request.getID(), Host);
                        try {
                            switch (path){
                                case "/" -> {
                                    FileSender.setProt(prot);
                                    FileSender.setContentType("text/html");
                                    FileSender.setStatus(200);
                                    FileSender.sendFile(Method, new File(Configs.getMainDir(Host) + "/index.html"), request.out, request.getIP(), request.getID(), Host);
                                }
                                case "/getcp" -> {
                                    FileSender.setProt(prot);
                                    FileSender.setContentType("image/png");
                                    FileSender.setStatus(200);
                                    FileSender.sendFile(Method, new Captcha(request.getIP(),Host).image,request.out,request.getIP(),request.getID(),Host);
                                }
                                case "/chkcp" -> {
                                    FileSender.setProt(prot);
                                    FileSender.setStatus(200);
                                    FileSender.setContentType("text/plain");
                                    FileSender.send(Data.checkAnswer(request.getIP(),reqes.Body),request.out,request.getIP(),request.getID(),Host);
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
                                                Logger.glog("CGI script requested. preparing for executing ...   ; id = " + request.getID(), Host);
                                                List<String> cmds = new ArrayList<>();
                                                cmds.add(cmd);
                                                cmds.add(fl.getAbsolutePath());
                                                new CGIExecuter(cmds, fl, request.getID(), Host, headers, url, request.out, reqes.Body, request.getIP());
                                            } else {
                                                fl = new File(Configs.getMainDir(Host) + path);
                                                FileSender.setProt(prot);
                                                if (ext.equals(".js")) FileSender.setContentType(FileTypes.getContentType(".js"));
                                                else FileSender.setContentType("text/plain");
                                                FileSender.setStatus(200);
                                                FileSender.sendFile(Method, fl, request.out, request.getIP(), request.getID(), Host);
                                            }
                                        } else {
                                            fl = new File(Configs.getMainDir(Host) + path);
                                            if (fl.isFile()) {
                                                FileSender.setProt(prot);
                                                FileSender.setContentType(tempCntc);
                                                FileSender.setStatus(200);
                                                FileSender.sendFile(Method, fl, request.out, request.getIP(), request.getID(), Host);
                                            } else this.sendCode(404);
                                        }
                                    } else {
                                        fl = new File(Configs.getMainDir(Host) + path);
                                        if (fl.isFile()) {
                                            FileSender.setProt(prot);
                                            FileSender.setContentType(FileTypes.getContentType(".bin"));
                                            FileSender.setStatus(200);
                                            FileSender.sendFile(Method, fl, request.out, request.getIP(), request.getID(), Host);
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
            }else this.sendCode(reqes.sit);
        }else{
            FileSender.setProt(prot);
            FileSender.setStatus(200);
            FileSender.setContentType("text/plain");
            FileSender.send(null,request.out,request.getIP(),request.getID(),Host);
        }
    }
    
    private void sendCode(int code){
        FileSender.setProt(prot);
        FileSender.setContentType("text/html");
        FileSender.setStatus(code);
        FileSender.sendFile(Method,new File(Configs.getCWD() + "/default_pages/" + code + ".html"),request.out,request.getIP(),request.getID(),Host);
    }

}