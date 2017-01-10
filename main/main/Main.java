package main;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class Main {

	private static JSONObject readAuthData(){
		JSONParser parser = new JSONParser(); 
		JSONObject authData = null;
		try {
			authData = (JSONObject) parser.parse(new FileReader("token.json"));
		} catch (FileNotFoundException e) {
			System.out.println("Could not find login credentials data.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println("Could not parse login credentials data.");
			e.printStackTrace();
		} 
		return authData;
	}

	public static void main(String[] args) {
		// Create an appropriately sized blocking queue
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);

		// Define our endpoint: By default, delimited=length is set (we need this for our processor)
		// and stall warnings are on.
		StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();
		endpoint.stallWarnings(false);

		// Read login data from configuration file
		JSONObject authData = readAuthData();

		Authentication auth = new OAuth1(
				(String) authData.get("consumerKey"), (String) authData.get("consumerSecret"),
				(String) authData.get("accessToken"), (String) authData.get("accessTokenSecret")
				);

		// Create a new BasicClient. By default gzip is enabled.
		BasicClient client = new ClientBuilder()
				.name("twitterClient")
				.hosts(Constants.STREAM_HOST)
				.endpoint(endpoint)
				.authentication(auth)
				.processor(new StringDelimitedProcessor(queue))
				.build();

		// Establish a connection
		client.connect();
		
		while(true){
			if(client.isDone()) break;
			System.out.println("Client connection closed unexpectedly: " + client.getExitEvent().getMessage());
			queue.poll();
		}
		
		client.stop();
	}

}