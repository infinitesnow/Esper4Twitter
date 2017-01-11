package main;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Throwables;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class MessageManager {
	public final int SLEEP_TIMER=20;
	public final int THREADS=8;
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

	public void processStream() throws InterruptedException{
		twitterManager = new TwitterManager(msgQueue);
		esperManager = new EsperManager();
		ExecutorService executor = Executors.newFixedThreadPool(THREADS);
		for (int i=0; i<THREADS; i++)
			executor.submit(
					() -> 
					{
						while(true){
							if(twitterManager.isDone()) break;
							pollQueue();
							try {
								logger.trace("In queue: " + msgQueue.size() + "messages. Sleeping");
								Thread.sleep(SLEEP_TIMER);
							} catch (InterruptedException e) {
								logger.warn("Thread interrupted while sleeping");
								logger.debug(Throwables.getStackTraceAsString(e));
							}
						}
					});
	}

	private void pollQueue() {
		String msg=null;
		synchronized (this) {
			if(msgQueue.isEmpty()) return;
			msg=msgQueue.poll();
		}
		handleMessage(msg);
	}

	public void handleMessage(String msg) {

		logger.trace("Received tweet, processing");

		// Parse the tweet
		JsonParser parser = new JsonParser(); 
		JsonObject parsedMessage=null;
		try {
			parsedMessage = (JsonObject) parser.parse(msg);
			if(parsedMessage==null) throw new Exception("Received null message");
		} catch (JsonParseException e) {
			logger.warn("Failed to parse message.");
			logger.debug(Throwables.getStackTraceAsString(e));
			return;
		} catch (Exception e) {
			logger.warn("Parsed message is null.");
			logger.debug(Throwables.getStackTraceAsString(e));
			return;
		}

		// If it's not a tweet, return
		if(parsedMessage.get("created_at")==null) {
			logger.trace("Not a tweet, ignoring");
			tweetLogger.debug("(notatweet)");
			return;
		}

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
			tweetLogger.error("(Invalid message!");
			return;
		}
		try{
			JsonObject user = parsedMessage.getAsJsonObject("user");
			user_idstr = user.get("id_str").getAsString();
			user_name = user.get("name").getAsString();
		} catch(NullPointerException e){
			logger.error("Received an invalid message: user, user.id_str or user.name is empty."
					+ "\nMessage:" + parsedMessage.getAsString());
			tweetLogger.error("(Invalid message!");
			return;
		}

		// Check for media
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
					logger.trace("Picture found!");;
				}
			}
			logger.trace("Finished processing media array");
		} catch(NullPointerException e){
			// Does not contain media, do nothing
		}

		// Print to log
		synchronized(this){
			tweetLogger.info("\nMessage " + idstr + ": " + text + "\nBy " + user_name + " " + user_idstr + ". Has picture: " + hasPicture);
			if (mediaList!=null) tweetLogger.info("Media: " + mediaList);
		}

		// Push to Esper stream
		TweetEvent tweet = new TweetEvent(idstr, text, user_idstr, user_name, hasPicture);
		esperManager.pushToEsper(tweet);
	}

}
