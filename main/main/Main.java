package main;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
	
	private static final Logger logger = LogManager.getLogger("AppLogger");

	public static void main(String[] args) throws InterruptedException {
		// First argument is stream type
		String streamType = args[0];
		// Create an appropriately sized blocking queue
		LinkedBlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(10000);
		TwitterManager twitterManager=null;
		try {
			twitterManager = new TwitterManager(streamType,msgQueue);
		} catch (Exception e) {
			logger.fatal(e.getMessage());
			return;
		}
		MessageManager messageManager = new MessageManager(twitterManager);
		messageManager.processStream();
	}

}