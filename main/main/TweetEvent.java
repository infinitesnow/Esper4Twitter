package main;

public class TweetEvent {
	public String getId_str() {
		return idstr;
	}
	public String getText() {
		return text;
	}
	public TweetEvent(String idstr, String text, String user_idstr, String user_name, boolean hasMedia) {
		super();
		this.idstr = idstr;
		this.text = text;
		this.user_idstr = user_idstr;
		this.user_name = user_name;
		this.hasMedia = hasMedia;
	}
	public String getUser_id_str() {
		return user_idstr;
	}
	public String getUser_name() {
		return user_name;
	}
	public boolean hasMedia() {
		return hasMedia;
	}
	private String idstr;
	private String text;
	private String user_idstr;
	private String user_name;
	private boolean hasMedia;
	
}
