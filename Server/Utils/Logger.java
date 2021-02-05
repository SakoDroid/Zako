package Server.Utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static String host;
    private static final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");

    private static class LoggerThread extends Thread{

        private final String log;
        private String filename;
        private final int logType;

        public LoggerThread(String log,int logType){
            this.log = log;
            this.logType = logType;
            this.start();
        }

        public LoggerThread(String log,int logType,String filename){
            this.log = log;
            this.logType = logType;
            this.filename = filename;
            this.start();
        }

        @Override
        public void run(){
            String logDir = "";
            String platform = System.getProperty("os.name").toLowerCase();
            if (platform.contains("linux"))
                logDir = "/var/log/zako-web";
            else if (platform.contains("windows"))
                logDir = System.getProperty("user.dir") + "/Logs";
            switch (this.logType){
                case 1 -> {
                    String out = df.format(new Date()) + " | " + log + "\n------------------------------------\n";
                    writeInFile(new File(logDir + "/access.log"),out);
                }
                case 2 -> {
                    String out = df.format(new Date()) + " | " + log + "\n------------------------------------\n";
                    writeInFile(new File(logDir + "/Internal-Logs.log"),out);
                }
                case 3 -> {
                    String out = df.format(new Date()) + " | " + filename + " | " + log + "\n------------------------------------\n";
                    writeInFile(new File(logDir + "/CGI-Logs.log"),out);
                }
                case 4 -> {
                    String out = df.format(new Date()) + " | " + filename + " | " + log + "\n------------------------------------\n";
                    writeInFile(new File(logDir + "/CGI-Errors.log"),out);
                }
                case 5 -> {
                    String out = df.format(new Date()) + " | " + log + "\n------------------------------------\n";
                    writeInFile(new File(logDir + "/Threat.log"),out);
                }
            }
        }
    }
    public static void glog(String log,String hst){
        host = hst;
        Thread t = new LoggerThread(log,1);
    }

    public static void tlog(String log){
        Thread t = new LoggerThread(log,5);
    }

    public static void ilog(String log){
        if (!log.endsWith("Read timed out") && !log.endsWith("Socket closed")) new LoggerThread(log,2);
    }

    public static void CGILog(String log,String filename,String hst){
        host = hst;
        Thread t = new LoggerThread(log,3,filename);
    }

    public static void CGIError(String error,String filename,String hst){
        host = hst;
        Thread t = new LoggerThread(error,4,filename);
    }

    public static void logException(Exception ex){
        new ExceptionLogger(ex);
    }

    private synchronized static void writeInFile(File fl,String data){
        try(FileWriter glog = new FileWriter(fl,true)){
            glog.write(data);
            glog.flush();
        }catch(Exception ex){
            System.out.println(ex.toString());
        }
    }

    private static class ExceptionLogger extends Thread{

        private final Exception exc;

        public ExceptionLogger(Exception ex){
            this.exc = ex;
            this.start();
        }

        @Override
        public void run(){
            StringBuilder t = new StringBuilder();
            for (StackTraceElement a : exc.getStackTrace()){
                t.append(a.toString()).append("  ;;  ");
            }
            t.append(exc.toString());
            Logger.ilog(t.toString());
        }
    }
}