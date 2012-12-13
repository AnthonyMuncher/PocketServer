package pocketserver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ConsoleLogManager {

    private static final Logger logger = Logger.getLogger("PocketServer");
    
    static void init() {
	ConsoleLogFormatter clf = new ConsoleLogFormatter();
	logger.setUseParentHandlers(false);
	ConsoleHandler consolehandler = new ConsoleHandler();
	consolehandler.setFormatter(clf);
	logger.addHandler(consolehandler);
	logger.setLevel(Level.FINEST);
	consolehandler.setLevel(Level.FINE);
	
	try {
	    FileHandler filehandler = new FileHandler("server.log",true);
	    filehandler.setFormatter(clf);
	    logger.addHandler(filehandler);
	} catch (Exception e) {
	    logger.log(Level.WARNING, "Failed to log to server.log", e);
	}
    }

    private static class ConsoleLogFormatter extends Formatter {

	private SimpleDateFormat dateFormat;
	
	public ConsoleLogFormatter() {
	    dateFormat = new SimpleDateFormat("HH:mm:ss");
	}
	
	public String format(LogRecord record) {
	    StringBuilder sb = new StringBuilder();
	    sb.append(dateFormat.format(Long.valueOf(record.getMillis())));
	    Level level = record.getLevel();
	    if(level == Level.FINEST) {
		sb.append(" [FINEST] ");
	    } else if(level == Level.FINER) {
		sb.append(" [FINER] ");
	    } else if(level == Level.FINE) {
		sb.append(" [FINE] ");
	    } else if(level == Level.INFO) {
		sb.append(" [INFO] ");
	    } else if(level == Level.WARNING) {
		sb.append(" [WARNING] ");
	    } else if(level == Level.SEVERE) {
		sb.append(" [SEVERE] ");
	    } else if(level == Level.SEVERE) {
		sb.append((new StringBuilder()).append(" [").append(level.getLocalizedName()).append("] ").toString());
	    } 
	    sb.append(record.getMessage());
	    sb.append('\n');
	    Throwable t = record.getThrown();
	    if (t != null) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		sb.append(sw.toString());
	    }
	    return sb.toString();
	}
    }

}
