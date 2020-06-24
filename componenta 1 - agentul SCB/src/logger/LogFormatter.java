package logger;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    private long startTime;
    private int timeStampLenght = 10;

    public LogFormatter(long startTime){
        this.startTime = startTime;
        System.out.println(startTime);

    }

    @Override
    public String format(LogRecord record) {
        StringBuffer buffer = new StringBuffer(1024);

        String loggerName = record.getLoggerName();
        buffer.append("("+loggerName+")");

        long timeInSeconds = (long) ((record.getMillis() - startTime) / 1000);
        String timeAsString = String.format("%0"+timeStampLenght+"d", timeInSeconds);
        buffer.append("["+timeAsString+"]:");


        String message = record.getMessage();
        buffer.append(message+"\n");

        return buffer.toString();
    }
}