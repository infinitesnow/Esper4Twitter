package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class TweetListener implements UpdateListener {

	private static final Logger esperLogger = LogManager.getLogger("EsperLogger");
	private static final Logger logger = LogManager.getLogger("AppLogger");
	
	public void update(EventBean[] incomingEvents, EventBean[] outgoingEvents) {
		// Ignore null event
		if (incomingEvents==null) {
			logger.trace("Null event, ignoring...");
			return;
		} else {
			logger.trace("Outputting with generic format");
			output(incomingEvents);
		}
	}
	
	private void output(EventBean[] incomingEvents){
		for (EventBean e : incomingEvents) {
			esperLogger.info(e.getUnderlying().toString());
		}
	}
}
