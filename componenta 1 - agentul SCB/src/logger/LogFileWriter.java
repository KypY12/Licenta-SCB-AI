package logger;

import com.springrts.ai.oo.clb.Log;

import java.io.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogFileWriter {

    private String aiVersion = "0.1";
    private String aiName = "SCB";
    private String logFilePath;

    private Logger logger = Logger.getLogger("MyLogger");
    private FileHandler fileHandler;
    private LogFormatter logFormatter;

    public LogFileWriter(Log springLogger, String logFileName){
        logFilePath = "./AI/Skirmish/" + aiName + "/" + aiVersion + "/log/" + logFileName;
        springLogger.log("------------------------------------------------" + logFilePath);

        try {
            fileHandler = new FileHandler(logFilePath);
            logFormatter = new LogFormatter(System.currentTimeMillis());
            fileHandler.setFormatter(logFormatter);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);

        } catch (Exception e) {
            springLogger.log("\n" + getStringStackTrace(e));
            springLogger.log("FINISH ============================");
        }

    }

    public void writeToLogFile(Level level, String message){
        logger.log(level, message);
    }


    public String getStringStackTrace(Exception e){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        return sw.toString();
    }

}
