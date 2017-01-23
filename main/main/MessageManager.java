package main;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import beans.MyTweet;

public class MessageManager {
	public static int SLEEP_TIMER=200;
	public static final int THREADS=8;
	private EsperManager esperManager;
	private TwitterManager twitterManager; 
	private BlockingQueue<String> msgQueue;
	private static final Logger logger = LogManager.getLogger("AppLogger");
	private static final Logger tweetLogger = LogManager.getLogger("TweetLogger");
	
	public MessageManager(TwitterManager twitterManager, EsperManager esperManager) {
		this.twitterManager=twitterManager;
		this.esperManager=esperManager;
		this.msgQueue=twitterManager.getMsgQueue();
	}

	public void processStream(EsperManager esperManager) {
		logger.info("Starting to process stream...");
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
		MyTweet tweet=null;
		try {
			tweet = new Gson().fromJson(msg, MyTweet.class);
			if(tweet==null) throw new Exception("Received null message");
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
		if(tweet.getCreated_at()==null) {
			logger.trace("Not a tweet, ignoring");
			tweetLogger.debug("(notatweet)");
			return;
		}

		// Print to log
		synchronized(this){
			tweetLogger.info("\nMessage " + tweet.getId_str() + ": " + tweet.getText() + "\nBy " + tweet.getUser().getName() + ", ID " + tweet.getUser().getId_str() + ". Has picture: " + tweet.isHasPicture());
			logger.trace("\nMessage " + tweet.getId_str() + ": " + tweet.getText() + "\nBy " + tweet.getUser().getName() + ", ID " + tweet.getUser().getId_str() + ". Has picture: " + tweet.isHasPicture() +"\n");
			tweetLogger.debug("Full message: " + msg);
			logger.trace("Full, unparsed message: " + msg);
		}

		// Push to Esper stream
		esperManager.pushToEsper(tweet);
	}

}
