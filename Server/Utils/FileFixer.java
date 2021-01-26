package Server.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.*;

public class FileFixer{

    private final File tf;

    public FileFixer(ArrayList<String[]> files, File tempFile) {
        this.tf = tempFile;
        for (String[] file : files) new FileThread(file);
    }

    private class FileThread extends Thread {

        private final String[] fl;

        public FileThread (String[] file) {
            this.fl = file;
            this.start();
        }

        @Override
        public void run() {
            try {
                int ex = 1;
                String name = fl[0];
                File tmp = new File(name);
                while(tmp.exists()){
                    ex++;
                    Pattern ptn = Pattern.compile("\\.\\w+");
                    Matcher mc = ptn.matcher(name);
                    String ext = "";
                    if (mc.find()) ext = mc.group();
                    name = fl[0].replace(ext,"") + ex + ext;
                    tmp = new File(name);
                }
                RandomAccessFile in = new RandomAccessFile(tf,"r");
                FileOutputStream out = new FileOutputStream(name, true);
                long on = Long.parseLong(fl[1]);
                long off = Long.parseLong(fl[2]);
                in.seek(on);
                byte[] b = new byte[(int)(off - on)];
                in.read(b,0,(int)(off - on));
                out.write(b);
                out.flush();
                out.close();
                in.close();
            } catch (Exception ex) {
                Logger.logException(ex);
            }
        }
    }
}