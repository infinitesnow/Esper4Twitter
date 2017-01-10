package main;

public class TweetEvent {
	public TweetEvent(String idstr, String text, String user_idstr, String user_name, boolean hasMedia) {
		super();
		this.idstr = idstr;
		this.text = text;
		this.user_idstr = user_idstr;
		this.user_name = user_name;
		this.hasMedia = hasMedia;
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
	public boolean isHasMedia() {
		return hasMedia;
	}
	private String idstr;
	private String text;
	private String user_idstr;
	private String user_name;
	private boolean hasMedia;
	
	@Override
	public String toString() {
		String event = "TweetEvent [ idstr=" + idstr + ", text=" + text
				+ ", user_idstr=" + user_idstr + ", user_name=" + user_name + ", hasMedia=" + hasMedia + " ]";
		System.err.println("Parsed event: " + event);
		return event;
	}
	
}
