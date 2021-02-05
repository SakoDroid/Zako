package Server.Utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");

    private static class LoggerThread extends Thread{

        private final String hostName;
        private final String log;
        private String filename;
        private final int logType;

        public LoggerThread(String log,int logType,String host){
            this.hostName = host;
            this.log = log;
            this.logType = logType;
            this.start();
        }

        public LoggerThread(String log,int logType,String filename,String host){
            this.hostName =  host;
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
                    String out = df.format(new Date()) + " | " + hostName + " | " + log +
                            "\n------------------------------------\n";
                    writeInFile(new File(logDir + "/access.log"),out);
                }
                case 2 -> {
                    String out = df.format(new Date()) + " | " + log +
                            "\n------------------------------------\n";
                    writeInFile(new File(logDir + "/Internal-Logs.log"),out);
                }
                case 3 -> {
                    String out = df.format(new Date()) + " | " + filename + " | "  + hostName + " | " + log +
                            "\n------------------------------------\n";
                    writeInFile(new File(logDir + "/CGI-Logs.log"),out);
                }
                case 4 -> {
                    String out = df.format(new Date()) + " | " + filename + " | " + hostName + " | " +  log +
                            "\n------------------------------------\n";
                    writeInFile(new File(logDir + "/CGI-Errors.log"),out);
                }
                case 5 -> {
                    String out = df.format(new Date()) + " | " + hostName + " | " +  log +
                            "\n------------------------------------\n";
                    writeInFile(new File(logDir + "/Threat.log"),out);
                }
            }
        }

        private void writeInFile(File fl,String data){
            try(FileWriter glog = new FileWriter(fl,true)){
                glog.write(data);
                glog.flush();
            }catch(Exception ex){
                System.out.println(ex.toString());
            }
        }
    }
    public static void glog(String log,String hst){
        new LoggerThread(log,1,hst);
    }

    public static void tlog(String log,String hst){
        new LoggerThread(log,5,hst);
    }

    public static void ilog(String log){
        if (!log.endsWith("Read timed out") && !log.endsWith("Socket closed")) new LoggerThread(log,2,"");
    }

    public static void CGILog(String log,String filename,String hst){
        new LoggerThread(log,3,filename,hst);
    }

    public static void CGIError(String error,String filename,String hst){
        new LoggerThread(error,4,filename,hst);
    }

    public static void logException(Exception ex){
        new ExceptionLogger(ex);
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