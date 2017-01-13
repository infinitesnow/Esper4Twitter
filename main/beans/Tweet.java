package beans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import beans.Entities.Media;

public class Tweet {

	private static final Logger logger = LogManager.getLogger("AppLogger");

	public class Coordinates{
		private double[] coordinates;
		public double[] getCoordinates() {
			return coordinates;
		}
		private String type;
		public String getType() {
			return type;
		}
	}

	public class Current_user_retweet{
		private double id;
		private String id_str;
		public double getId() {
			return id;
		}
		public String getId_str() {
			return id_str;
		}
	}

	private Coordinates coordinates;
	private String created_at;
	private Current_user_retweet current_user_retweet;
	private Entities entities;
	private int favorite_count;
	private boolean favorited;
	private String filter_level;
	private long id;
	private String id_str;
	private String in_reply_to_screen_name;
	private long in_reply_to_status_id;
	private String in_reply_to_status_id_str;
	private long in_reply_to_user_id;
	private String in_reply_to_user_id_str;
	private String lang;
	private Places places;
	private boolean possibly_sensitive;
	private long quoted_status_id;
	private String quoted_status_id_str;
	private Tweet quoted_status;
	private int retweet_count;
	private boolean retweeted;
	private Tweet retweeted_status;
	private String source;
	private String text;
	private boolean truncated;
	private User user;
	private String[] withheld_in_countries;
	private String withheld_scope;

	private boolean withheld_copyright;

	public Coordinates getCoordinates() {
		return coordinates;
	}

	public String getCreated_at() {
		return created_at;
	}

	public Current_user_retweet getCurrent_user_retweet() {
		return current_user_retweet;
	}

	public Entities getEntities() {
		return entities;
	}

	public int getFavorite_count() {
		return favorite_count;
	}

	public boolean isFavorited() {
		return favorited;
	}

	public String getFilter_level() {
		return filter_level;
	}

	public long getId() {
		return id;
	}

	public String getId_str() {
		return id_str;
	}

	public String getIn_reply_to_screen_name() {
		return in_reply_to_screen_name;
	}

	public long getIn_reply_to_status_id() {
		return in_reply_to_status_id;
	}

	public String getIn_reply_to_status_id_str() {
		return in_reply_to_status_id_str;
	}

	public long getIn_reply_to_user_id() {
		return in_reply_to_user_id;
	}

	public String getIn_reply_to_user_id_str() {
		return in_reply_to_user_id_str;
	}

	public String getLang() {
		return lang;
	}

	public Places getPlaces() {
		return places;
	}

	public boolean isPossibly_sensitive() {
		return possibly_sensitive;
	}

	public long getQuoted_status_id() {
		return quoted_status_id;
	}

	public String getQuoted_status_id_str() {
		return quoted_status_id_str;
	}

	public Tweet getQuoted_status() {
		return quoted_status;
	}

	public int getRetweet_count() {
		return retweet_count;
	}

	public boolean isRetweeted() {
		return retweeted;
	}

	public Tweet getRetweeted_status() {
		return retweeted_status;
	}

	public String getSource() {
		return source;
	}

	public String getText() {
		return text;
	}

	public boolean isTruncated() {
		return truncated;
	}

	public User getUser() {
		return user;
	}

	public boolean isWithheld_copyright() {
		return withheld_copyright;
	}

	public String[] getWithheld_in_countries() {
		return withheld_in_countries;
	}

	public String getWithheld_scope() {
		return withheld_scope;
	}

	public boolean isHasPicture(){
		// Check for media
		boolean hasPicture=false;
		try{
			Media[] mediaList = this.entities.getMedia();
			// Browse media and search for a picture
			logger.trace("Starting to process media array...");
			for (Media media : mediaList){
				String thisMediaType = media.getType();
				logger.trace("Found media of type: " + thisMediaType);
				if(thisMediaType.equals("photo")){
					logger.trace("Picture found!");
					hasPicture=true;
				}
			}
			logger.trace("Finished processing media array");
		} catch(NullPointerException e){
			// Does not contain media, do nothing
			return false;
		}

		if(hasPicture==true)
			return true;
		else {
			logger.error("Something went wrong searching for a picture in a tweet");
			return false;
		}
	}

}
