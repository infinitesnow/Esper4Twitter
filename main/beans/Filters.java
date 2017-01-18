package beans;

import java.util.List;

import com.twitter.hbc.core.endpoint.Location;

public class Filters {
	private List<Long> followings;
	private List<String> terms;
	private List<Location> locations;

	public List<Long> getFollowings() {
		return followings;
	}
	public List<String> getTerms() {
		return terms;
	}
	public List<Location> getLocations() {
		return locations;
	}
}
