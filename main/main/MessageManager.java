package main;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class MessageManager {
	private EsperManager esperManager;
	private TwitterManager twitterManager; 
	private BlockingQueue<String> msgQueue;
	private static final Logger logger = LogManager.getLogger("AppLogger");
	private static final Logger tweetLogger = LogManager.getLogger("TweetLogger");
	public BlockingQueue<String> getMsgQueue() {
		return msgQueue;
	}

	public MessageManager(){
		// Create an appropriately sized blocking queue
		msgQueue = new LinkedBlockingQueue<String>(10000);
	}
	
	public void processStream(){
		twitterManager = new TwitterManager(msgQueue);
		esperManager = new EsperManager();
		while(true){
			if(twitterManager.isDone()) break;
			handleMessage(msgQueue.poll());
		}
	}

	public void handleMessage(String msg) {
		// Parse the tweet
		if (msg==null) return;
		JsonParser parser = new JsonParser(); 
		JsonObject parsedMessage=null;
		try {
			parsedMessage = (JsonObject) parser.parse(msg);
			if(parsedMessage==null) throw new Exception("Received null message");
		} catch (JsonParseException e) {
			System.err.println("Failed to parse message.");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Parsed message is null.");
			e.printStackTrace();
			return;
		}
		
		// If it's a delete status, return
		if(parsedMessage.get("delete")!=null) {
			logger.trace("Delete status received, ignoring");
			tweetLogger.debug("(delete status)");
			return;
		}
		logger.trace("Received tweet, processing");
		
		// Get values from fields
		String idstr;
		String text;
		String user_idstr;
		String user_name;
		try{
			idstr = parsedMessage.get("id_str").getAsString();
			text = parsedMessage.get("text").getAsString();
		} catch(NullPointerException e){
			logger.error("Received an invalid message: id_str or text is empty."
					+ "\nMessage: " + parsedMessage.getAsString());
			tweetLogger.warn("(Invalid message!");
			return;
		}
		try{
			JsonObject user = parsedMessage.getAsJsonObject("user");
			user_idstr = user.get("id_str").getAsString();
			user_name = user.get("name").getAsString();
		} catch(NullPointerException e){
			logger.error("Received an invalid message: user, user.id_str or user.name is empty."
					+ "\nMessage:" + parsedMessage.getAsString());
			tweetLogger.warn("(Invalid message!");
			return;
		}
		JsonArray mediaList=null;
		boolean hasPicture=false;
		try{
			JsonObject entities = parsedMessage.getAsJsonObject("entities");
			mediaList = entities.getAsJsonArray("media");
			// Browse media and search for a picture
			logger.trace("Starting to process media array...");
			for (JsonElement aMedia : mediaList){
				String thisMediaType = aMedia.getAsJsonObject().get("type").getAsString();
				logger.trace("Found media of type: " + thisMediaType);
				if(thisMediaType.equals("photo")){
					hasPicture=true;
					logger.debug("Picture found!");;
				}
			}
			logger.trace("Finished processing media array");
		} catch(NullPointerException e){
			
		}
		
		// Print to log
		tweetLogger.info("\nMessage " + idstr + ": " + text + "\nBy " + user_name + " " + user_idstr + ". Has picture: " + hasPicture);
		if (mediaList!=null) tweetLogger.info("Media: " + mediaList);
		
		// Push to Esper stream
		TweetEvent tweet = new TweetEvent(idstr, text, user_idstr, user_name, hasPicture);
		logger.trace("Pushing event to Esper");
		esperManager.pushToEsper(tweet);
	}
	
}
