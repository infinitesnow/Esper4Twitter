package beans;

public class Entities {
	public class Hashtags{
		private int[] indices;
		private String text;
		
		public int[] getIndices() {
			return indices;
		}
		public String getText() {
			return text;
		}
	}
	
	public class Media{
		public class Sizes{
			public class Size{
				private int h;
				private String resize;
				
				public int getH() {
					return h;
				}
				public String getResize() {
					return resize;
				}
				public int getW() {
					return w;
				}
				private int w;
			}
			
			private Size thumb;
			private Size large;
			private Size medium;
			
			public Size getThumb() {
				return thumb;
			}
			public Size getLarge() {
				return large;
			}
			public Size getMedium() {
				return medium;
			}
			public Size getSmall() {
				return small;
			}
			private Size small; 			
		}
		
		private String display_url;
		private String expanded_url;
		private long id;
		private String id_str;
		private int[] indices;
		private String media_url;
		private String media_url_https;
		private Sizes sizes;
		private long source_status_id;
		private String source_status_id_str;
		private String type;
		private String url;
		
		public String getDisplay_url() {
			return display_url;
		}
		public String getExpanded_url() {
			return expanded_url;
		}
		public long getId() {
			return id;
		}
		public String getId_str() {
			return id_str;
		}
		public int[] getIndices() {
			return indices;
		}
		public String getMedia_url() {
			return media_url;
		}
		public String getMedia_url_https() {
			return media_url_https;
		}
		public Sizes getSizes() {
			return sizes;
		}
		public long getSource_status_id() {
			return source_status_id;
		}
		public String getSource_status_id_str() {
			return source_status_id_str;
		}
		public String getType() {
			return type;
		}
		public String getUrl() {
			return url;
		}
	}
	
	public class Urls{
		private String display_url;
		private String expanded_url;
		private int[] indices;
		private String url;
		
		public String getDisplay_url() {
			return display_url;
		}
		public String getExpanded_url() {
			return expanded_url;
		}
		public int[] getIndices() {
			return indices;
		}
		public String getUrl() {
			return url;
		}
	}
	public class User_mentions{
		private long id;
		private String id_str;
		private int[] indices;
		private String name;
		private String screen_name;

		public long getId() {
			return id;
		}
		public String getId_str() {
			return id_str;
		}
		public int[] getIndices() {
			return indices;
		}
		public String getName() {
			return name;
		}
		public String getScreen_name() {
			return screen_name;
		}
	}
	
	private Hashtags[] hashtags;
	private Media[] media;
	private Urls[] urls;
	private User_mentions[] user_mentions;

	public Hashtags[] getHashtags() {
		return hashtags;
	}
	public Media[] getMedia() {
		return media;
	}
	public Urls[] getUrls() {
		return urls;
	}
	public User_mentions[] getUser_mentions() {
		return user_mentions;
	}
}
