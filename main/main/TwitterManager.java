package main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.BlockingQueue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterManager {
	
	private JsonObject readAuthData(){
		JsonParser parser = new JsonParser(); 
		JsonObject authData = null;
		try {
			authData = (JsonObject) parser.parse(new FileReader("token.json"));
		} catch (FileNotFoundException e) {
			System.out.println("Could not find login credentials data.");
			e.printStackTrace();
		} catch (JsonParseException e) {
			System.out.println("Could not parse login credentials data.");
			e.printStackTrace();
		} 
		return authData;
	}
	
	private BasicClient client;
	
	private void initializeConnection(BlockingQueue<String> msgQueue) {
		// Define our endpoint: By default, delimited=length is set (we need this for our processor)
		// and stall warnings are on.
		StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();
		// serstreamEndpoint endpoint = new UserstreamEndpoint();

		// Read login data from configuration file
		JsonObject authData = readAuthData();
		// Print them to console
		System.err.println("Using authentication data: \n Consumer key:" + authData.get("consumerKey").getAsString() + 
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
				.endpoint(endpoint)
				.authentication(auth)
				.processor(new StringDelimitedProcessor(msgQueue))
				.build();

		// Establish a connection
		client.connect();
	}

	public TwitterManager(BlockingQueue<String> msgQueue) {
		initializeConnection(msgQueue);
	}

	public boolean isDone() {
		if(client.isDone()){ 
			System.out.println("Client connection closed unexpectedly: " + client.getExitEvent().getMessage());
			client.stop();
			return true;
		}
		return false;
	}
}
