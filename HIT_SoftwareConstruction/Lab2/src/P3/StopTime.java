
/**
 * represents stop and bus arrival time
 * immutable
 * @author 
 */
public class StopTime implements Stop {

	private final String name;
	private final double latitude;
	private final double longtitude;
	private final int time;
	
	/**
	 * Constructor
	 * @param name name of stop
	 * @param latitude latitude of stop
	 * @param longtitude longitude of stop
	 * @param stationMap station map records 
	 * arrival time of bus to destination place
	 */
	public StopTime(String name, double latitude, 
			double longtitude, int time) {
		this.name = name;
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.time = time;
	}
	
	public StopTime(Stop stop, int time) {
		this.name = stop.getName();
		this.latitude = stop.getLatitude();
		this.longtitude = stop.getLongitude();
		this.time = time;
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

	/**
	 * get arrival time of bus
	 * @return arrival time
	 */
	public int getTime() {
		return this.time;
	}
	
	public Stop getStop() {
		return Stop.getInstance(this); 
	}
	
	/**
	 * equals method to compare stop location
	 * @param stop stop to compare with
	 * @return true if stop and this stopTime has the same name and location
	 */
	public boolean stopEquals(Stop stop) {
		return getName().equals(stop.getName()) && 
				getLatitude() == stop.getLatitude() && 
				getLongitude() == stop.getLongitude();
	}
	
	@Override
	public int hashCode() {
		return name.hashCode() + 5 * Double.valueOf(latitude).hashCode()
				+ 7 * Double.valueOf(longtitude).hashCode() + 
				31 * Integer.valueOf(time).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof StopTime) {
			StopTime stopTime = (StopTime)obj;
			return this.stopEquals(stopTime) && this.time == stopTime.time;
		} else {
			return false;
		}
	}
}
