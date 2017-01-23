package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Throwables;

public class Main {
	
	private static final Logger logger = LogManager.getLogger("AppLogger");

	public static void main(String[] args) throws InterruptedException {
		// First argument is stream type
		String streamType = args[0];
		TwitterManager twitterManager=null;
		EsperManager esperManager=null;
		MessageManager messageManager=null;
		try {
			twitterManager = new TwitterManager(streamType);
			esperManager = new EsperManager();
			messageManager = new MessageManager(twitterManager,esperManager);
		} catch (Exception e) {
			logger.fatal(e.getMessage());
			logger.debug(Throwables.getStackTraceAsString(e));
			return;
		}
		messageManager.processStream(esperManager);
	}

}