package main;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class TweetListener implements UpdateListener {

	public void update(EventBean[] incomingEvents, EventBean[] outgoingEvents) {
		
		for (EventBean e : incomingEvents) {
			System.out.println(e.getUnderlying());
		}
		
	}
}
