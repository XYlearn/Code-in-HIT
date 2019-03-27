
/**
 * represent a trip segment
 * 
 * @author 
 *
 */
public interface TripSegment {

	/**
	 * get start location of this segment
	 * 
	 * @return start location of this segment
	 */
	public Stop getStartLocation();

	/**
	 * get end location of this segment
	 * 
	 * @return end location of this segment
	 */
	public Stop getEndLocation();

	/**
	 * get start time of this segment
	 * 
	 * @return start time of this segment
	 */
	public int getStartTime();

	/**
	 * get end time of this segment
	 * 
	 * @return end time of this segment
	 */
	public int getEndTime();

	/**
	 * get duration of segment
	 * 
	 * @return duration of segment
	 */
	public int getDuration();

}
