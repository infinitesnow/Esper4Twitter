package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class TweetListener implements UpdateListener {

	private static final Logger esperLogger = LogManager.getLogger("EsperLogger");
	private static final Logger logger = LogManager.getLogger("AppLogger");
	private final String outputStringFormat;

	public TweetListener(String string) {
		this.outputStringFormat=string;
	}

	public void update(EventBean[] incomingEvents, EventBean[] outgoingEvents) {
		// Ignore null event
		if (incomingEvents==null) return;
		// If outputStringFormat is set, output with format
		if (outputStringFormat!=null) {
			outputFormat(incomingEvents);
			return;
		}
		for (EventBean e : incomingEvents) {
			logger.info("Received query output, outputting to log");
			esperLogger.info(e.getUnderlying().toString());
		}
	}

	private void outputFormat(EventBean[] incomingEvents) {
		for (EventBean e : incomingEvents) {
			logger.info("Received query output, outputting with format to log");
			esperLogger.info(String.format(outputStringFormat,e.get("MYVALUE")));
		}
	}
}
