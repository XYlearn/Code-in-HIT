
/**
 * represents a trip segment for waiting bus
 * 
 * @author XHWhy
 */
public class WaitSegment implements TripSegment {
	private final Stop stop;
	private final int startTime;
	private final int endTime;

	/**
	 * AF:
	 * 	use Stop to save wait location
	 * 	save start time and end time as integer
	 * RI:
	 * 	startTime > endTime
	 * safety from rep exposure:
	 * 	all fields are private final immutable types
	 */
	
	/**
	 * constructor
	 * 
	 * @param stop
	 *            stop wait in
	 * @param startTime
	 *            start time of wait segment, startTime >= 0
	 * @param endTime
	 *            end time of wait segment, endTime > 0
	 */
	public WaitSegment(Stop stop, int startTime, int endTime) {
		this.stop = stop;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	@Override
	public Stop getStartLocation() {
		return stop;
	}

	@Override
	public Stop getEndLocation() {
		return stop;
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
