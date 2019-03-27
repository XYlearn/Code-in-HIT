
/**
 * represents a trip segment from one bus stop to adjacent bus stop
 * 
 * @author XHWhy
 *
 */
public class BusSegment implements TripSegment {
	
	/**
	 * AF:
	 * 	use Stop to save start and end location
	 * 	save start time and end time as integer
	 * RI:
	 * 	startTime > endTime
	 * safety from rep exposure:
	 * 	all fields are private final immutable types
	 */

	private final Stop startLocation;
	private final Stop endLocation;
	private final int startTime;
	private final int endTime;

	/**
	 * constructor
	 * 
	 * @param startLocation
	 *            start location of busSegment
	 * @param endLocation
	 *            end location of busSegment
	 * @param startTime
	 *            start time of busSegment, startTime >= 0
	 * @param endTime
	 *            end time of busSegment, endTime > 0
	 */
	public BusSegment(Stop startLocation, Stop endLocation, int startTime, int endTime) {
		this.startLocation = startLocation;
		this.endLocation = endLocation;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	@Override
	public Stop getStartLocation() {
		return this.startLocation;
	}

	@Override
	public Stop getEndLocation() {
		return this.endLocation;
	}

	@Override
	public int getStartTime() {
		return this.startTime;
	}

	@Override
	public int getEndTime() {
		return this.endTime;
	}

	@Override
	public int getDuration() {
		return endTime - startTime;
	}
}
