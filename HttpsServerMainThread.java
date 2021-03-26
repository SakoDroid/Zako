import Engines.DDOS.Interface;
import Server.HttpListener;
import Server.Reqandres.Request.Request;
import Server.Reqandres.Senders.QuickSender;
import Server.Utils.*;
import Server.Utils.Configs.Perms;
import Server.Utils.Configs.SSLConfigs;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.*;

public class HttpsServerMainThread{

    private final String host;

    public HttpsServerMainThread(int port,String host){
        this.host = host;
        new HTTPS();
        new HTTP();
    }

    private class HTTP extends Thread{

        public HTTP(){
            this.start();
        }

        private class Redirect extends Thread{

            private final Socket sock;

            public Redirect(Socket s){
                this.sock = s;
                try{
                    if (Perms.isIPAllowed(s.getInetAddress().getHostAddress())){
                        if (Interface.checkIP(s.getInetAddress().getHostAddress(),host))
                            this.start();
                        else{
                            Logger.glog(s.getInetAddress().getHostAddress() + " request rejected due to DDOS protection." + "  ; id = " + 0 , host);
                            s.getOutputStream().write(HTMLGen.genTooManyRequests(s.getInetAddress().getHostAddress()).getBytes());
                            s.getOutputStream().flush();
                            s.getOutputStream().close();
                        }
                    }else{
                        Logger.glog(s.getInetAddress().getHostAddress() + " request rejected due to ip ban." + "  ; id = " + "  ; id = " + 0 , host);
                        s.getOutputStream().write(HTMLGen.genIPBan(s.getInetAddress().getHostAddress()).getBytes());
                        s.getOutputStream().flush();
                        s.getOutputStream().close();
                    }
                }catch (Exception ex){
                    Logger.logException(ex);
                }
            }

            @Override
            public void run(){
                try{
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                    String firstLine = bfr.readLine();
                    if (firstLine != null && firstLine.length() > 5){
                        Pattern pathPattern = Pattern.compile(" /[^ ]*");
                        Matcher pathMatcher = pathPattern.matcher(firstLine);
                        if (pathMatcher.find()){
                            String path = pathMatcher.group().trim();
                            String upHeader;
                            boolean hostFound = false;
                            boolean upFound = false;
                            String line = "";
                            while (!hostFound){
                                line = bfr.readLine();
                                if (line != null){
                                    if (line.startsWith("Host"))
                                        hostFound = true;
                                    else if (line.startsWith("Upgrade-Insecure-Requests"))
                                        upFound = true;
                                    else if (line.length() < 5)
                                        break;
                                }else
                                    break;
                            }
                            if (hostFound){
                                String hostHeader = line.split(":")[1].trim();
                                if (hostHeader.equals(host)){
                                    if (!upFound){
                                        while (!upFound){
                                            line = bfr.readLine();
                                            if (line != null){
                                                if (line.startsWith("Upgrade-Insecure-Requests"))
                                                    upFound = true;
                                                else if (line.length() < 5)
                                                    break;
                                            }else
                                                break;
                                        }
                                    }
                                    if (upFound){
                                            upHeader = line.split(":")[1].trim();
                                            if (upHeader.equals("1"))
                                                this.red("https://" + host + path);
                                            else{
                                                DataOutputStream out = new DataOutputStream(sock.getOutputStream());
                                                out.writeBytes("HTTP/1.1 426 Upgrade Required\nServer: " + basicUtils.Zako + "\nDate: " + new java.util.Date().toString() + "\nConnection: upgrade\nUpgrade: TLS/1.3, HTTP/1.1\r\n\r\n");
                                            }
                                    }else
                                        this.red("https://" + host + path);
                                }else {
                                    Interface.addWarning(sock.getInetAddress().getHostAddress(),host);
                                    new QuickSender(new Request(sock)).sendBadReq("\"Host\" header conflict.");
                                }
                            }else {
                                Interface.addWarning(sock.getInetAddress().getHostAddress(),host);
                                new QuickSender(new Request(sock)).sendBadReq("\"Host\" header not found.");
                            }
                        }else{
                            Interface.addWarning(sock.getInetAddress().getHostAddress(),host);
                            new QuickSender(new Request(sock)).sendBadReq("Path not found in first line.");
                        }
                    }
                }catch(Exception ex){
                    Logger.logException(ex);
                }
            }

            private void red(String path){
                try{
                    DataOutputStream out = new DataOutputStream(sock.getOutputStream());
                    out.writeBytes("HTTP/1.1 301 Moved Permanently\nServer: " + basicUtils.Zako + "\nDate: " + new java.util.Date().toString() + "\nLocation: " + URLDecoder.decode(path, StandardCharsets.UTF_8) + "\r\n\r\n");
                }catch (Exception ex){
                    Logger.logException(ex);
                }
            }

        }

        @Override
        public void run(){
            try{
                ServerSocket ss = new ServerSocket(80);
                Logger.ilog("Http thread is running on port 80 for redirection ...");
                System.out.println("Http thread is running on port 80 for redirection ...");
                while (true) new Redirect(ss.accept());
            }catch (Exception ex) {
                Logger.logException(ex);
            }
        }
    }

    private class HTTPS extends Thread{

        public HTTPS(){
            this.start();
        }

        @Override
        public void run () {
            try {
                System.setProperty("javax.net.ssl.keyStore", SSLConfigs.getJKS(host));
                System.setProperty("javax.net.ssl.keyStorePassword", SSLConfigs.getPass(host));
                SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
                SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketfactory.createServerSocket(443);
                Logger.ilog("Https server thread is now running (jks : " + SSLConfigs.getJKS(host) + " ,, jks pass : " + SSLConfigs.getPass(host) + ") ...");
                System.out.println("Https server thread is now running (jks : " + SSLConfigs.getJKS(host) + " ,, jks pass : " + SSLConfigs.getPass(host) + ") ...");
                while (true) new HttpListener((SSLSocket) sslServerSocket.accept(),host,false);
            } catch (Exception ex) {
                Logger.logException(ex);
            }
            Logger.ilog("Server is shutting down ...");
            System.out.println("Server is shutting down ...");
        }
    }
}
