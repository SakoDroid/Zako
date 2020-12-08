package Server.Utils;

import java.io.*;
import java.util.ArrayList;

public class FileFixer extends Thread {

    private ArrayList<String[]> files;
    private File tf;

    public FileFixer(ArrayList<String[]> files, File tempFile){
        this.files = files;
        this.tf = tempFile;
        this.start();
    }

    @Override
    public void run(){
        try{
            for (String[] file : this.files){
                BufferedReader in = new BufferedReader(new FileReader(tf));
                FileOutputStream out = new FileOutputStream(file[0],true);
                long on = Long.parseLong(file[1]);
                long off = Long.parseLong(file[2]);
                in.skip(on);
                for (long i = on ; i < off-1 ; i++){
                    out.write(in.read());
                }
                out.flush();
                out.close();
                in.close();
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
}