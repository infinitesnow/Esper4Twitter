package main;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.espertech.esper.client.EventBean;

public class TweetListenerFormat extends TweetListener{

	private static final Logger esperLogger = LogManager.getLogger("EsperLogger");
	private static final Logger logger = LogManager.getLogger("AppLogger");
	private final String outputFormat;
	private final List<String> argumentNames;

	public TweetListenerFormat(String outputFormat, List<String> argumentNames) {
		this.outputFormat=outputFormat;
		this.argumentNames=argumentNames;
	}


	public void update(EventBean[] incomingEvents, EventBean[] outgoingEvents) {
		// Ignore null event
		if (incomingEvents==null) {
			logger.trace("Null event, ignoring...");
			return;
		} else {
			logger.trace("Received event, processing output");
			logger.trace("Format is set, outputting with format");
			outputFormat(incomingEvents);
		}
	}

	private void outputFormat(EventBean[] incomingEvents) {
		for (EventBean e : incomingEvents) {
			logger.trace("Parsing an EventBean...");
			String[] outputArguments = new String[argumentNames.size()];
			int i=0;
			for (String argumentName : argumentNames){
				logger.trace("Getting output argument with name " + argumentName);
				outputArguments[i]=e.get(argumentName).toString();
				logger.trace("OK");
				i++;
			}
			logger.info("Outputting with format to log");
			esperLogger.info(String.format(outputFormat,(Object) outputArguments));
		}
	}
}
