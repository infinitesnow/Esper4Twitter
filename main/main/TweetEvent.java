package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TweetEvent {
	private static final Logger logger = LogManager.getLogger("AppLogger");

	public TweetEvent(String idstr, String text, String user_idstr, String user_name, boolean hasPicture) {
		super();
		this.idstr = idstr;
		this.text = text;
		this.user_idstr = user_idstr;
		this.user_name = user_name;
		this.hasPicture = hasPicture;
	}
	public String getIdstr() {
		return idstr;
	}
	public String getText() {
		return text;
	}
	public String getUser_idstr() {
		return user_idstr;
	}
	public String getUser_name() {
		return user_name;
	}
	public boolean isHasPicture() {
		return hasPicture;
	}
	private String idstr;
	private String text;
	private String user_idstr;
	private String user_name;
	private boolean hasPicture;
	
	@Override
	public String toString() {
		String event = "TweetEvent [ idstr=" + idstr + ", text=" + text
				+ ", user_idstr=" + user_idstr + ", user_name=" + user_name + ", hasPicture=" + hasPicture + " ]";
		logger.trace("Parsed event: " + event);
		return event;
	}
	
}
