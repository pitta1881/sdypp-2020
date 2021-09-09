package Ej2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {
	public static Logger logger;
	public ArrayList <logVolatil> logMemoria = new ArrayList<logVolatil>();
	FileHandler fh;
	
	static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-4s] %5$s %n");
        logger = Logger.getLogger(Log.class.getName());
    }
	
	public Log(String file_name) throws SecurityException, IOException{
		File f = new File(file_name);
		if(!f.exists()) {
			f.createNewFile();
		}
		
		fh = new FileHandler(file_name, true);
		logger = Logger.getLogger("test");
		logger.addHandler(fh);
		logger.setLevel(Level.INFO);
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);
	}
	
	public void addToLog(String msg, String miNivel) {
		switch (miNivel) {
		case "INFO":
			logger.info(msg);
			break;
		case "WARN":
			logger.warning(msg);
			break;
		case "SEVERE":
			logger.severe(msg);
			break;
		default:
			break;
		}        
		logMemoria.add(new logVolatil(miNivel,msg));
    }
	
	public ArrayList<logVolatil> getLogVolatil(){
		return this.logMemoria;
	}

}
