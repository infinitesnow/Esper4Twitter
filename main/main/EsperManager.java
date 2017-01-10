package main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

public class EsperManager {
	
	private EPRuntime cepRT;
	private EPStatement cepStatement;
	private EPServiceProvider cep;
	private EPAdministrator cepAdm;
	private Configuration cepConfig;
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
			FileReader fileReader = new FileReader("query.conf");
			query = fileReader.toString();
			fileReader.close();
		} catch (FileNotFoundException e) {
			System.err.println("No query configuration file found");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Input error while trying to read query from file.");
			e.printStackTrace();
		}
		System.err.println(query);
		
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
