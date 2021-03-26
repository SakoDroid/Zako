package Server.Utils.Reader;

import Server.Reqandres.Request.Request;
import Server.Utils.Configs.Configs;
import Server.Utils.FileFixer;
import Server.Utils.Logger;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.regex.*;

public class BodyParser {

    private final Request req;

    public BodyParser(Request reqs){
        this.req = reqs;
    }

    public void parseBody(){
        try(RandomAccessFile bf = new RandomAccessFile(req.getCacheFile(), "r")){
            Pattern ptn = Pattern.compile("boundary=[^\n]+");
            Matcher mc = ptn.matcher(req.getHeaders().get("Content-Type"));
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
                            String filead = Configs.getUploadDir(this.req.getHost()) + "/" + fileName;
                            if(namemc.find()) this.req.addToBody('&' + namemc.group().replace("name=","").replace("\"","") + "=" + filead);
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
                            if (off - on < Configs.getFileSize(req.getHost())) files.add(file);
                        }else if (namemc.find()){
                            bf.readLine();
                            String val = bf.readLine();
                            if(!val.isEmpty()) this.req.addToBody('&' + namemc.group().replace("name=\"","") + "=" + val);
                        }
                    }
                }
            }else {
                bf.read();
                byte[] temp = new byte[(int)(bf.length() - bf.getFilePointer())];
                bf.read(temp);
                this.req.addToBody(temp);
            }
            bf.close();
            if (!files.isEmpty()) new FileFixer(files,req.getCacheFile());
        }catch(Exception ex){
            Logger.logException(ex);
        }
    }
}
