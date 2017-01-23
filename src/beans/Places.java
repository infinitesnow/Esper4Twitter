package beans;

import beans.Tweet.Coordinates;

public class Places {
	public class Attributes{
		private String street_address;
		private String locality;
		private String region;
		private String iso3;
		private String postal_code;
		
		public String getStreet_address() {
			return street_address;
		}
		public String getLocality() {
			return locality;
		}
		public String getRegion() {
			return region;
		}
		public String getIso3() {
			return iso3;
		}
		public String getPostal_code() {
			return postal_code;
		}
		public String getPhone() {
			return phone;
		}
		public String getTwitter() {
			return twitter;
		}
		public String getUrl() {
			return url;
		}
		private String phone;
		private String twitter;
		private String url;
	}
	
	private Attributes attributes;
	private Coordinates[][] bounding_box;
	private String country;
	private String country_code;
	private String full_name;
	private String id;
	private String name;
	private String place_type;
	private String url;
	
	public Attributes getAttributes() {
		return attributes;
	}
	public Coordinates[][] getBounding_box() {
		return bounding_box;
	}
	public String getCountry() {
		return country;
	}
	public String getCountry_code() {
		return country_code;
	}
	public String getFull_name() {
		return full_name;
	}
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getPlace_type() {
		return place_type;
	}
	public String getUrl() {
		return url;
	}
	
}
