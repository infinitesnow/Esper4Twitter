package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.google.common.base.Throwables;

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

		// Get queries from Json file
		List<String> queryList=null;
		try{
			queryList=getQueries();
		} catch (FileNotFoundException e) {
			logger.error("No query configuration file found");
			logger.debug(Throwables.getStackTraceAsString(e));
			return;
		} catch (Exception e) {
			logger.error("Input error while trying to read query from file.");
			logger.debug(Throwables.getStackTraceAsString(e));
			return;
		}
		
		// Insert the queries into Esper
		insertQueries(queryList);

		logger.trace("Finished parsing queries.");

		// The EP runtime API allows to send events into different streams
		cepRT = cep.getEPRuntime();

	}

	private void insertQueries(List<String> queryList) {
		for (String query : queryList){
			// Create listener output header
			String patternString="FORMAT\\s+\\\"(.*?)\\\"\\s+";
			logger.debug("Pattern string: " + patternString);
			Pattern pattern = Pattern.compile(patternString);
			Matcher  matcher = pattern.matcher(query);
			if(!matcher.find()){
				logger.warn("No valid format found, trying to insert query as generic");
				insertGenericQuery(query);
				continue;
				}
			String format = matcher.group(1);
			logger.debug("Format: " + format);
			// Prepare query
			query=query.replaceAll("FORMAT\\s+\\\".*?\\\"\\s+","");
			logger.debug("Inserting query: " + query);
			// Create statement and attach a listener to it
			cepStatement = cepAdm.createEPL(query);
			cepStatement.addListener(new TweetListener(format));
			logger.debug("Done.");
		}
	}

	private void insertGenericQuery(String query) {
		logger.debug("Inserting generic query: " + query);
		// Create statement and attach a listener to it
		cepStatement = cepAdm.createEPL(query);
		cepStatement.addListener(new TweetListener(null));
		logger.debug("Done.");
	}

	private List<String> getQueries() throws IOException {
		List<String> queryList = new ArrayList<String>();
		for (File queryFile : FileUtils.listFiles(new File("./config/queries/"), FileFilterUtils.trueFileFilter(), null)){
			String query = FileUtils.readFileToString(queryFile, StandardCharsets.UTF_8);
			queryList.add(query);
			logger.trace("Found query:\n" + query);
		}
		return queryList;
	}

	public void pushToEsper(TweetEvent tweet) {
		logger.trace("Pushing event to Esper: " + tweet.toString());
		cepRT.sendEvent(tweet);
		logger.trace("Pushed successfully.");
	}
}
