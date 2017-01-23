package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import com.twitter.hbc.core.endpoint.UserstreamEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import beans.Filters;

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
	private Authentication auth; 
	private LinkedBlockingQueue<String> msgQueue;
	
	public LinkedBlockingQueue<String> getMsgQueue() {
		return msgQueue;
	}

	public TwitterManager(String streamType) throws Exception {

		// Create an appropriately sized blocking queue
		this.msgQueue = new LinkedBlockingQueue<String>(10000);

		// Read login data from configuration file
		JsonObject authData = readAuthData();
		// Print them to console
		logger.debug("Using authentication data: \n consumerKey:" + authData.get("consumerKey").getAsString() + 
				"\n consumerSecret:" + authData.get("consumerSecret").getAsString() + 
				"\n accessToken: " + authData.get("accessToken").getAsString() + 
				"\n accessTokenSecret: " + authData.get("accessTokenSecret").getAsString());
		auth = new OAuth1(
				authData.get("consumerKey").getAsString(), authData.get("consumerSecret").getAsString(),
				authData.get("accessToken").getAsString(), authData.get("accessTokenSecret").getAsString()
				);

		// Define our endpoint: By default, delimited=length is set (we need this for our processor)
		// and stall warnings are on.
		switch (streamType){
		case "USER" : 
			createUserEndpoint();
			break;
		case "SAMPLE" : 
			createSampleEndpoint();
			break;
		case "FILTER" :
			createFilterEndpoint();
			break;
		default: 
			throw new Exception("No valid argument. Please run with SAMPLE, USER or FILTER as argument.");
		}

		// Establish a connection
		logger.info("Client created, connecting.");
		client.connect();
		logger.info("Done.");
		
	}

	private void createUserEndpoint(){
		logger.trace("Creating User endpoint...");
		UserstreamEndpoint uendpoint = (UserstreamEndpoint) new UserstreamEndpoint();
		logger.trace("OK");
		createClient(uendpoint, Constants.USERSTREAM_HOST);
	}

	private void createSampleEndpoint(){
		logger.trace("Creating Sample endpoint...");
		StatusesSampleEndpoint sendpoint = (StatusesSampleEndpoint) new StatusesSampleEndpoint();
		logger.trace("OK");
		createClient(sendpoint, Constants.STREAM_HOST);
	}

	private void createFilterEndpoint() throws Exception{
		logger.trace("Creating Filter endpoint...");
		StatusesFilterEndpoint fendpoint = (StatusesFilterEndpoint) new StatusesFilterEndpoint();
		logger.trace("Searching for filter configuration file");
		Filters filters=null;
		try {
			filters = new Gson().fromJson( FileUtils.readFileToString(new File("./config/filters.json"), StandardCharsets.UTF_8), Filters.class);
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			logger.fatal("Cannot find a valid filter configuration file.");
			logger.debug(Throwables.getStackTraceAsString(e));
			return;
		}
		logger.info("Found filters:\nUsers: " + filters.getFollowings() + "\nTerms: " + filters.getTerms() + "\nLocations: " + filters.getLocations());
		if(filters.getTerms()!=null)
			fendpoint.trackTerms(filters.getTerms());
		if(filters.getFollowings()!=null)
			fendpoint.followings(filters.getFollowings());
		if(filters.getLocations()!=null)
			fendpoint.locations(filters.getLocations());
		logger.trace("OK");
		createClient(fendpoint, Constants.STREAM_HOST);
	}
	
	private void createClient(StreamingEndpoint endpoint, String HOST){
		logger.trace("Creating client...");
		// Create a new BasicClient. By default gzip is enabled.
		client = new ClientBuilder()
				.name("twitterClient")
				.hosts(HOST)
				.endpoint(endpoint)
				.authentication(auth)
				.processor(new StringDelimitedProcessor(msgQueue))
				.build();
		logger.trace("OK");
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
