package main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Throwables;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.endpoint.UserstreamEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

@SuppressWarnings("unused")
public class TwitterManager {

	private static final Logger logger = LogManager.getLogger("AppLogger");

	private JsonObject readAuthData(){
		JsonParser parser = new JsonParser(); 
		JsonObject authData = null;
		try {
			authData = (JsonObject) parser.parse(new FileReader("./config/token.json"));
		} catch (FileNotFoundException e) {
			logger.fatal("Could not find login credentials data.");
			logger.debug(Throwables.getStackTraceAsString(e));
		} catch (JsonParseException e) {
			logger.fatal("Could not parse login credentials data.");
			logger.debug(Throwables.getStackTraceAsString(e));
		} 
		return authData;
	}

	private BasicClient client;

	public TwitterManager(BlockingQueue<String> msgQueue) {
		
		// Define our endpoint: By default, delimited=length is set (we need this for our processor)
		// and stall warnings are on.
		StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();
		//UserstreamEndpoint endpoint = new UserstreamEndpoint();

		// Read login data from configuration file
		JsonObject authData = readAuthData();
		// Print them to console
		logger.debug("Using authentication data: \n Consumer key:" + authData.get("consumerKey").getAsString() + 
				"\n Consumer secret:" + authData.get("consumerSecret").getAsString() + 
				"\n accessToken" + authData.get("accessToken").getAsString() + 
				"\n accessTokenSecret" + authData.get("accessTokenSecret").getAsString());
		Authentication auth = new OAuth1(
				authData.get("consumerKey").getAsString(), authData.get("consumerSecret").getAsString(),
				authData.get("accessToken").getAsString(), authData.get("accessTokenSecret").getAsString()
				);

		// Create a new BasicClient. By default gzip is enabled.
		client = new ClientBuilder()
				.name("twitterClient")
				.hosts(Constants.STREAM_HOST)
		//		.hosts(Constants.USERSTREAM_HOST)
				.endpoint(endpoint)
				.authentication(auth)
				.processor(new StringDelimitedProcessor(msgQueue))
				.build();

		// Establish a connection
		client.connect();
	}

	public boolean isDone() {
		if(client.isDone()){ 
			logger.info("Client connection closed unexpectedly: " + client.getExitEvent().getMessage());
			client.stop();
			return true;
		}
		return false;
	}
}
