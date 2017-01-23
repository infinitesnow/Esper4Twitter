package beans;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Throwables;

import beans.Entities.Hashtags;
import beans.Entities.Media;

public class MyTweet extends Tweet{

	private static final Logger logger = LogManager.getLogger("AppLogger");

	public boolean hasPicture(){
		// Check for media
		try{
			Media[] mediaList = this.getEntities().getMedia();
			// Browse media and search for a picture
			logger.trace("Starting to process media array...");
			for (Media media : mediaList){
				String thisMediaType = media.getType();
				logger.trace("Found media of type: " + thisMediaType);
				if(thisMediaType.equals("photo")){
					logger.trace("Picture found!");
					return true;
				}
			}
			logger.trace("Finished processing media array");
		} catch(NullPointerException e){
			// Does not contain media, do nothing
			logger.debug("Caught NullPointerException, assuming empty media array");
			logger.trace(Throwables.getStackTraceAsString(e));
			return false;
		}
		return false;
	}
	
	public boolean hasHashtag(String hashtag){
		// Check presence of hashtag
		try{
			Hashtags[] hashtagList = this.getEntities().getHashtags();
			// Browse hashtags and search for the argument
			logger.trace("Starting to process hashtags array...");
			for (Hashtags aHashtag : hashtagList){
				logger.trace("Found hashtag: " + aHashtag.getText());
				if(aHashtag.getText().equals(hashtag)){
					logger.trace("Hashtag found!");
					return true;
				}
			}
			logger.trace("Finished processing hashtags array");
		} catch(NullPointerException e){
			// Does not contain media, do nothing
			logger.debug("Caught NullPointerException, assuming empty hashtags array");
			logger.trace(Throwables.getStackTraceAsString(e));
			return false;
		}
		return false;
	}

}