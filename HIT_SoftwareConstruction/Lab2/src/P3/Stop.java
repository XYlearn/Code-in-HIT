
/**
 * represents a bus stop
 */
public interface Stop {
	/**
	 * get stop name
	 * 
	 * @return stop name
	 */
	public String getName();

	/**
	 * get latitude of stop
	 * 
	 * @return latitude of stop
	 */
	public double getLatitude();

	/**
	 * get longitude of stop
	 * 
	 * @return longitude of stop
	 */
	public double getLongitude();
	
	public static Stop getInstance(String name, double latitude, double longtitude) {
		return new SimpleStop(name, latitude, longtitude);
	}
	
	public static Stop getInstance(Stop stop) {
		return new SimpleStop(stop.getName(), stop.getLatitude(), stop.getLongitude());
	}
	
	public static boolean stopEquals(Stop stop1, Stop stop2) {
		return stop1.getName().equals(stop2.getName()) &&
				stop1.getLatitude() == stop2.getLatitude() &&
				stop1.getLongitude() == stop2.getLongitude();
	}
}

class SimpleStop implements Stop {
	private final String name;
	private final double latitude;
	private final double longtitude;
	
	SimpleStop(String name, double latitude, double longtitude) {
		this.name = name;
		this.latitude = latitude;
		this.longtitude = longtitude;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public double getLatitude() {
		return this.latitude;
	}

	@Override
	public double getLongitude() {
		return this.longtitude;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode() + 7 * Double.valueOf(latitude).hashCode()
				+ 31 * Double.valueOf(longtitude).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Stop) {
			return Stop.stopEquals((Stop)obj, this);
		} else {
			return false;
		}
	}
}
