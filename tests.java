import Engines.FCGI.Client.FCGIClient;
import Engines.FCGI.Client.Response.FCGIResponse;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.http.WebSocket;
import java.security.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class tests {


    public static void main(String[] args){
        /*HashMap<String,String> contentTypes = new HashMap();
        contentTypes.put(".aac","audio/aac");
        contentTypes.put(".js","application/javascript");
        contentTypes.put(".css","text/css");
        contentTypes.put(".csv","text/csv");
        contentTypes.put(".html","text/html");
        contentTypes.put(".xml","application/xml");
        contentTypes.put(".fxml","application/xml");
        contentTypes.put(".zip","application/zip");
        contentTypes.put(".pdf","application/pdf");
        contentTypes.put(".json","application/json");
        contentTypes.put(".abw","application/x-abiword");
        contentTypes.put(".arc","application/x-freearc");
        contentTypes.put(".avi","video/x-msvideo");
        contentTypes.put(".azw","application/vnd.amazon.ebook");
        contentTypes.put(".bin","application/octet-stream");
        contentTypes.put(".bmp","image/bmp");
        contentTypes.put(".bz","application/x-bzip");
        contentTypes.put(".bz2","application/x-bzip2");
        contentTypes.put(".csh","application/x-csh");
        contentTypes.put(".doc","application/msword");
        contentTypes.put(".docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        contentTypes.put(".eot","application/vnd.ms-fontobject");
        contentTypes.put(".epub","application/epub+zip");
        contentTypes.put(".gz","application/gzip");
        contentTypes.put(".gif","image/gif");
        contentTypes.put(".htm","text/html");
        contentTypes.put(".ico","image/vnd.microsoft.icon");
        contentTypes.put(".ics","text/calendar");
        contentTypes.put(".jar","application/java-archive");
        contentTypes.put(".jpeg","image/jpeg");
        contentTypes.put(".jpg","image/jpeg");
        contentTypes.put(".jsonld","application/ld+json");
        contentTypes.put(".mid","audio/midi");
        contentTypes.put(".midi","audio/midi");
        contentTypes.put(".mjs","text/javascript");
        contentTypes.put(".mp3","audio/mpeg");
        contentTypes.put(".mpeg","video/mpeg");
        contentTypes.put(".mpkg","application/vnd.apple.installer+xml");
        contentTypes.put(".odp","application/vnd.oasis.opendocument.presentation");
        contentTypes.put(".ods","application/vnd.oasis.opendocument.spreadsheet");
        contentTypes.put(".odt","application/vnd.oasis.opendocument.text");
        contentTypes.put(".oga","audio/ogg");
        contentTypes.put(".ogv","video/ogv");
        contentTypes.put(".ogx","application/ogg");
        contentTypes.put(".opus","audio/opus");
        contentTypes.put(".otf","font/otf");
        contentTypes.put(".png","image/png");
        contentTypes.put(".php","CGI");
        contentTypes.put(".ppt","application/vnd.ms-powerpoint");
        contentTypes.put(".pptx","application/vnd.openxmlformats-officedocument.presentationml.presentation");
        contentTypes.put(".rar","application/vnd.rar");
        contentTypes.put(".rtf","application/rtf");
        contentTypes.put(".sh","application/s-sh");
        contentTypes.put(".svg","image/svg+xml");
        contentTypes.put(".swf","application/x-shockwave-flash");
        contentTypes.put(".tar","application/x-tar");
        contentTypes.put(".tif","image/tiff");
        contentTypes.put(".tiff","image/tiff");
        contentTypes.put(".ts","video/mp2t");
        contentTypes.put(".ttf","font/ttf");
        contentTypes.put(".txt","text/plain");
        contentTypes.put(".vsd","application/vnd.visio");
        contentTypes.put(".wav","audio/wav");
        contentTypes.put(".weba","audio/webm");
        contentTypes.put(".webm","video/webm");
        contentTypes.put(".webp","image/webp");
        contentTypes.put(".woff","font/woff");
        contentTypes.put(".woff2","font/woff2");
        contentTypes.put(".xhtml","application/xhtml+xml");
        contentTypes.put(".xls","application/vnd.ms-excel");
        contentTypes.put(".xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        contentTypes.put(".xul","application/vnd.mozilla.xul+xml");
        contentTypes.put(".7z","application/x-7z-compressed");
        contentTypes.put(".py","CGI");
        contentTypes.put(".cgi","CGI");
        try{
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("content-types.sak"));
            out.writeObject(contentTypes);
            out.flush();
            out.close();

            /*
            HashMap<String,String> cmds = new HashMap<>();
            cmds.put(".py","python3");
            cmds.put(".php","php");
            cmds.put(".cgi","bash");
            cmds.put(".pl","perl");
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("cmds.sak"));
            out.writeObject(cmds);
            out.flush();
            out.close();
            /*
        }catch(Exception ex){
            System.out.println(ex.toString());
        }*/

        /*try {
            SSLServerSocketFactory ssf = null;
            SSLContext ctx;
            KeyManagerFactory kmf;
            KeyStore ks;
            char[] passphrase = "changeit".toCharArray();

            ctx = SSLContext.getInstance("TLS");
            kmf = KeyManagerFactory.getInstance("SunX509");
            ks = KeyStore.getInstance("JKS");

            ks.load(new FileInputStream("/usr/lib/jvm/jdk-15/lib/security/cacerts"), passphrase);
            kmf.init(ks, passphrase);
            ctx.init(kmf.getKeyManagers(), null, null);

            ssf = ctx.getServerSocketFactory();;
            ServerSocket ss = ssf.createServerSocket(3000);
            while(true){
                Socket s = ss.accept();
                InputStream in = s.getInputStream();
                OutputStream out = s.getOutputStream();
                int i;
                while ((i = in.read()) != -1) System.out.print((char)i);
                out.flush();
                s.close();
            }




            ServerSocketChannel mn = ServerSocketChannel.open().bind(new InetSocketAddress(3000));
            SocketChannel sh = mn.accept();
            sh.configureBlocking(false);
            System.out.println(sh.socket().getInetAddress().getHostAddress());
            ByteBuffer bf = ByteBuffer.allocate(1024);
            FileChannel fh = FileChannel.open(Paths.get("Temp12345.tmp"),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
            while((sh.read(bf)) > 0) {
                bf.flip();
                fh.write(bf);
                bf.clear();
            }
            fh.close();
            sh.configureBlocking(true);
            DataOutputStream out = new DataOutputStream(sh.socket().getOutputStream());
            out.writeBytes("HTTP/1.1 200 OK\nContent-Type: text/plain\n\nshit");
            out.flush();
            out.close();
        }*/

        try{
            /*HashMap<String,String> mimes;
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("content-types.sak"));
            mimes = (HashMap<String, String>) in.readObject();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.newDocument();
            Element main = d.createElement("MIMEs");
            d.appendChild(main);
            for (String s : mimes.keySet()){
                Element mm = d.createElement("MIME");
                Element ext = d.createElement("extension");
                Element M = d.createElement("MIME-Type");
                ext.setTextContent(s);
                M.setTextContent(mimes.get(s));
                mm.appendChild(ext);
                mm.appendChild(M);
                main.appendChild(mm);
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(d);
            StreamResult result = new StreamResult(new File("cars.xml"));
            transformer.transform(source, result);*/
            /*ServerSocket ss = new ServerSocket(9000);
            while(true){
                Socket s = ss.accept();
                InputStream is = s.getInputStream();
                int i;
                while((i = is.read()) != -1){
                    if (i <= 126 && i >= 32) System.out.print((char)i);
                    else System.out.println("byte : " + i);
                }
            }*/

            String s = """
                    GATEWAY_INTERFACE : FastCGI/1.0
                    SERVER_PORT : 3000
                    SCRIPT_FILENAME : /mnt/E8A8DC6AA8DC3930/Projects/tmrin/src/cgi/phpinfo.php
                    SERVER_PROTOCOL : HTTP/1.1
                    DOCUMENT_ROOT : /mnt/E8A8DC6AA8DC3930/Projects/tmrin/src/cgi
                    REQUEST_URI : phpinfo.php
                    REMOTE_ADDR : 0:0:0:0:0:0:0:1
                    SERVER_SOFTWARE : Zako 0.1
                    REMOTE_PORT : 9985
                    QUERY_STRING :\s
                    CONTENT_TYPE : multipart/form-data; boundary=----WebKitFormBoundaryPLhJTzYbCpyVSNyR
                    REQUEST_METHOD : POST
                    SCRIPT_NAME : mnb.php
                    HTTP_USER_AGENT : Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36
                    HTTP_COOKIE : Webstorm-91c2f3e9=99aa5e67-bb35-4a36-8c1a-fd71adc026fa; Idea-ffd35ea2=cbdd6bb8-926c-46e3-b694-7cc409494e1c; Idea-ffd35ea3=ebcc74ec-b729-492f-b071-2227b8641c3e
                    SERVER_ADDR :\s
                    SERVER_NAME : localhost
                    PATH_INFO : /phpinfo.php""";

            String content = "name=john&address=beijing";
            String uri = "/phpinfo.php";
            Map<String, String> params = new HashMap<String, String>();
            /*String documentRoot = "/mnt/E8A8DC6AA8DC3930/Projects/tmrin/src/cgi";
            params.put("GATEWAY_INTERFACE", "FastCGI/1.0");
            params.put("REQUEST_METHOD", "POST");
            params.put("SCRIPT_FILENAME", documentRoot + uri);
            params.put("SCRIPT_NAME", uri);
            //params.put("QUERY_STRING", "");
            params.put("QUERY_STRING", "\s");
            //params.put("REQUEST_URI", uri);
            params.put("REQUEST_URI", "phpinfo.php");
            params.put("DOCUMENT_ROOT", documentRoot);
            //params.put("REMOTE_ADDR", "127.0.0.1");
            params.put("REMOTE_ADDR", "0:0:0:0:0:0:0:1");
            params.put("REMOTE_PORT", "9985");
            params.put("SERVER_ADDR", "127.0.0.1");
            params.put("SERVER_NAME", "localhost");
            //params.put("SERVER_PORT", "80");
            params.put("SERVER_PORT", "3000");
            params.put("SERVER_PROTOCOL", "HTTP/1.1");
            //params.put("CONTENT_TYPE", "application/x-www-form-urlencoded");
            params.put("CONTENT_TYPE", "multipart/form-data; boundary=----WebKitFormBoundaryPLhJTzYbCpyVSNyR");
            params.put("CONTENT_LENGTH", content.length() + "");
            params.put("SERVER_SOFTWARE","Zako 0.1");


            for (String str : s.split("\n")){
                String[] entry = str.split(":",2);
                params.put(entry[0].trim(),entry[1].trim());
            }
            params.put("CONTENT_LENGTH",String.valueOf(content.length()));
            System.out.println(params);
            FCGIClient cl = new FCGIClient(params,content,"gu","ker");
            cl.run();
            FCGIResponse res = cl.getResponse();
            System.out.println(res.toString());*/
            java.util.Timer writeTtimer = new java.util.Timer(false);
            writeTtimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("shit");
                }
            },1000);
        }catch (Exception ex){
            System.out.println(ex);
        }
    }


    public static byte[] hexStringToByteArray(String hex) {
        int l = hex.length();
        byte[] data = new byte[l / 2];
        for (int i = 0; i < l; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
