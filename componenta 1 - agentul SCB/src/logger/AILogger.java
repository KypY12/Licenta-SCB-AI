package logger;

import com.springrts.ai.oo.clb.Log;
import com.springrts.ai.oo.clb.OOAICallback;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;

// Loggerul creat pentru debugging mai usor
public class AILogger {

    private OOAICallback callback;
    private Log logger;
    private String logFileName = "log.info";

    private LogFileWriter logFileWriter;
    private LogConverter logConverter;

    public AILogger(OOAICallback callback) {
        this.callback = callback;
        this.logger = callback.getLog();
        this.logFileWriter = new LogFileWriter(this.logger, logFileName);
        this.logConverter = new LogConverter(callback);

    }

    public OOAICallback getCallback() {
        return this.callback;
    }

    public void log(Object obj){
        logFileWriter.writeToLogFile(Level.ALL, logConverter.getAsString(obj));
    }

}
