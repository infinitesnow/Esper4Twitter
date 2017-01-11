package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.util.FileUtil;

public class EsperManager {
	
	private EPRuntime cepRT;
	private EPStatement cepStatement;
	private EPServiceProvider cep;
	private EPAdministrator cepAdm;
	private Configuration cepConfig;
	private static final Logger logger = LogManager.getLogger("AppLogger");
	public EsperManager() {
		
		// Create configuration
		cepConfig = new Configuration();
		cepConfig.addEventType("TweetEvent", TweetEvent.class.getName());
		
		// Create Provider Manager with new configuration
		cep = EPServiceProviderManager.getProvider("Twitter Stream", cepConfig);
		
		// Through the EP administrator API we can manage the EPL Statement registration
		cepAdm = cep.getEPAdministrator();
		
		// Create query
		String query=null;
		try {
			query = FileUtil.readTextFile(new File("./config/query.conf"));
		} catch (FileNotFoundException e) {
			logger.error("No query configuration file found");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			logger.error("Input error while trying to read query from file.");
			e.printStackTrace();
			return;
		}
		logger.debug("Found query:\n" + query + "\n");
		
		// Create statement and attach a listener to it
		cepStatement = cepAdm.createEPL(query);
		cepStatement.addListener(new TweetListener());
		
		// The EP runtime API allows to send events into different streams
		cepRT = cep.getEPRuntime();

	}

	public void pushToEsper(TweetEvent tweet) {
		cepRT.sendEvent(tweet);
	}
}
