package Server.Utils;

import java.io.*;
import java.util.ArrayList;

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
                BufferedReader in = new BufferedReader(new FileReader(tf));
                FileOutputStream out = new FileOutputStream(fl[0], true);
                long on = Long.parseLong(fl[1]);
                long off = Long.parseLong(fl[2]);
                in.skip(on);
                for (long i = on; i < off - 1; i++) {
                    out.write(in.read());
                }
                out.flush();
                out.close();
                in.close();
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
}