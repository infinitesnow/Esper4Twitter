package main;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class MessageManager {
	private EsperManager esperManager;
	private TwitterManager twitterManager; 
	private BlockingQueue<String> msgQueue;
	public BlockingQueue<String> getMsgQueue() {
		return msgQueue;
	}

	public MessageManager(){
		// Create an appropriately sized blocking queue
		msgQueue = new LinkedBlockingQueue<String>(10000);
		//esperManager = new EsperManager();
	}
	
	public void processStream(){
		
		twitterManager = new TwitterManager(msgQueue);
		
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
		} catch (JsonParseException e) {
			System.out.println("Failed to parse message.");
			e.printStackTrace();
		}
		
		// Get values from fields
		String idstr = getString(parsedMessage.get("id_str"));
		String text = getString(parsedMessage.get("text"));
		JsonObject user = parsedMessage.get("user").getAsJsonObject();
		String user_idstr = getString(getElement(user,"idstr"));
		String user_name = getString(getElement(user,"name"));
		JsonObject mediaObject = getObject(parsedMessage.get("entities").getAsJsonObject(),"media");
		boolean hasMedia=false;
		if(mediaObject!=null) 
			hasMedia=true; 
		
		System.err.println("Message " + idstr + ": " + text + "\nBy " + user_name + " " + user_idstr);
		
		// Push to Esper stream
		esperManager.pushToEsper(new TweetEvent(idstr, text, user_idstr, user_name, hasMedia));
	}
	
	// This methods are safe for empty fields
	private String getString(JsonElement json){
		if(json==null) return "(empty)";
		else return json.getAsString();
	}
	private JsonObject getObject(JsonObject json,String obj){
		if(json==null) return null;
		else return json.getAsJsonObject(obj);
	}
	private JsonElement getElement(JsonObject json,String el){
		if(json==null) return null;
		else return json.get(el);
	}
}
