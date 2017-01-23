package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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

import beans.MyTweet;

public class EsperManager {

	private EPRuntime cepRT;
	private EPStatement cepStatement;
	private EPServiceProvider cep;
	private EPAdministrator cepAdm;
	private Configuration cepConfig;
	private static final Logger logger = LogManager.getLogger("AppLogger");
	public EsperManager() throws Exception {

		// Create configuration
		cepConfig = new Configuration();
		cepConfig.addEventType("MyTweet", MyTweet.class);

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

		// The EP runtime API allows to send events into different streams
		cepRT = cep.getEPRuntime();

	}

	private void insertQueries(List<String> queryList) throws Exception {
		int insertedQueries = 0;
		for (String query : queryList){
			// Parse the query
			QueryParser parser = new QueryParser(query);
			query=parser.getQuery();
			// Get output header format
			String format = parser.getFormat();
			// Get arguments
			List<String> argumentNames = parser.getArgumentNames();
			logger.info("Inserting query: " + query);
			// Create statement
			try{
				cepStatement = cepAdm.createEPL(query);
			} catch( Exception e){
				logger.error("Could not insert query.");
				logger.debug(Throwables.getStackTraceAsString(e));
				continue;
			}
			logger.debug("Query inserted correctly.");
			if(format==null){
				logger.warn("No valid format found, attaching generic listener.");
				attachListener(query);
			} else {
				logger.debug("Format found, attaching format listener.");
				attachFormatListener(query,format,argumentNames);
			}
			insertedQueries++;
			logger.info("Query OK.");
		}
		logger.info("Finished parsing queries.");
		if (insertedQueries==0)
			throw new Exception("No valid query found.");
	}

	private void attachFormatListener(String query, String format, List<String> argumentNames) {
		logger.debug("Attaching format listener");
		cepStatement.addListener(new TweetListenerFormat(format,argumentNames));
		logger.debug("Done.");
	}

	private void attachListener(String query) {
		logger.debug("Attaching generic listener");
		cepStatement.addListener(new TweetListener());
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

	public void pushToEsper(MyTweet tweet) {
		logger.trace("Pushing event to Esper: " + tweet.toString());
		cepRT.sendEvent(tweet);
		logger.trace("Pushed successfully.");
	}
}
