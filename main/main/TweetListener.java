package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class TweetListener implements UpdateListener {

	private static final Logger esperLogger = LogManager.getLogger("EsperLogger");
	private static final Logger logger = LogManager.getLogger("AppLogger");

	public void update(EventBean[] incomingEvents, EventBean[] outgoingEvents) {
		if (incomingEvents==null) return;
		for (EventBean e : incomingEvents) {
			logger.info("Received query output, outputting to log");
			esperLogger.info("Tweets with pictures: " + Float.parseFloat(e.get("ratio").toString())*100 + "%.");
		}
		
	}
}
