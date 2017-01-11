package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class TweetListener implements UpdateListener {

	private static final Logger esperLogger = LogManager.getLogger("EsperLogger");
	
	public void update(EventBean[] incomingEvents, EventBean[] outgoingEvents) {
		
		for (EventBean e : incomingEvents) {
			esperLogger.info(e.getUnderlying());
		}
		
	}
}
