package Server.Reqandres;

import Server.Utils.*;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;


public class Request {

    private RandomAccessFile bf;
    private Methods method;
    public String Body = "";
    public String rawHeaders = "";
    public String ip;
    public File tempFile;
    public HashMap headers;
    private int id;
    public String Host;
    public String Path;
    public int sit = 0;
    public int stat = 1;

    public Request(DataOutputStream out,InputStream in,int id,String ip){
        this.id = id;
        this.ip = ip;
        try{
            tempFile = new File(Configs.getCWD() + "/src/Temp/temp-" + id + ".tmp");
            FileWriter fw = new FileWriter(tempFile, true);
            while (true) {
                fw.write(in.read());
                if (in.available() == 0) break;
            }
            fw.flush();
            fw.close();
            bf = new RandomAccessFile(tempFile,"r");
            this.headers = this.parseHeaders();
            switch (this.method){
                case CONNECT -> this.sit = 200;
                case PUT -> this.handlePUT();
                case DELETE -> this.handleDELETE();
                default -> {
                    if (!basicUtils.LocalHostIP.isEmpty()) Host = Host.replace(basicUtils.LocalHostIP,Configs.MainHost);
                    Host = Host.replace("127.0.0.1","localhost");
                    int status = Configs.getHostStatus(Host);
                    if (status == 0) {
                        String[] api = APIConfigs.getAPIAddress(Host + Path);
                        if (api == null){
                            sit = Perms.isDirPerm(Configs.getMainDir(Host) + Path);
                            if (sit < 300) {
                                if (this.method == Methods.POST) {
                                    String postLength;
                                    if ((postLength = (String) headers.get("Content-Length")) != null) {
                                        long length = Long.parseLong(postLength);
                                        if (length < Configs.postBodySize) parseBody();
                                        else sit = 413;
                                    } else sit = 411;
                                } else bf.close();
                            }
                        }else{
                            if (api.length > 1){
                                stat = 0;
                                bf.close();
                                Logger.glog("request for API " + Host + Path + " received from " + ip + " .", Host);
                                new SubForwarder(api, tempFile, out, ip, Host + Path);
                            }else {
                                Path = api[0];
                            }
                        }
                    }else if (status == 1){
                        stat = 0;
                        bf.close();
                        Logger.glog("request for " + Host + " received from " + ip + " .",Host);
                        new SubForwarder(Configs.getForwardAddress(Host),tempFile,out,ip,Host);
                    }else sit = 500;
                }
            }
        }catch(IOException ex){
            this.sit = 500;
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
    }


    private HashMap parseHeaders(){
        HashMap head = new HashMap();
        try{
            String line;
            boolean firstlineparsed = false;
            while(!(line = bf.readLine()).isEmpty()){
                rawHeaders += line;
                if(!firstlineparsed){
                    String[] p = line.split(" ", 3);
                    String mth = p[0];
                    switch (mth){
                        case "GET" -> this.method = Methods.GET;
                        case "POST" -> this.method = Methods.POST;
                        case "PUT" -> this.method = Methods.PUT;
                        case "HEAD" -> this.method = Methods.HEAD;
                        case "DELETE" -> this.method = Methods.DELETE;
                        case "CONNECT" -> this.method = Methods.CONNECT;
                        case "OPTIONS" -> this.method = Methods.OPTIONS;
                        case "TRACE" -> this.method = Methods.TRACE;
                    }
                    head.put("Method", this.method);
                    head.put("URL", p[1]);
                    head.put("Version", p[2]);
                    firstlineparsed = true;
                }else{
                    String[] tmp = line.split(":", 2);
                    if (tmp.length != 1) head.put(tmp[0].trim(), tmp[1].trim());
                }
            }
            if(this.method != Methods.CONNECT && this.method != Methods.OPTIONS){
                String prt = (String) head.get("Version");
                String url = prt.split("/")[0] + "://" + head.get("Host") + URLDecoder.decode((String) head.get("URL"), StandardCharsets.UTF_8);
                URL u = new URL(url);
                Host = u.getHost().replace("www.", "").replace("ww2.", "");
                if (u.getPort() != -1) Host += ":" + u.getPort();
                Path = u.getPath();
                head.replace("URL", u);
            }
        }catch(Exception ex){
            this.sit = 500;
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()) {
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
        return head;
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
            this.sit = 500;
            String t = "";
            for (StackTraceElement a : ex.getStackTrace()){
                t += a.toString() + " ;; ";
            }
            t += ex.toString();
            Logger.ilog(t);
        }
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
            this.sit = 500;
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
                        ptn = Pattern.compile("filename=.*");
                        mc = ptn.matcher(detailLine);
                        Pattern nameptn = Pattern.compile("name=.*");
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
            if (!files.isEmpty()) new FileFixer(files,tempFile);
        }catch(Exception ex){
            this.sit = 500;
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