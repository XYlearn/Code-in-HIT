import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * represents an itinerary immutable
 * 
 * @author 
 *
 */
public class Itinerary {
	private final String name;
	private final List<TripSegment> segments;

	/**
	 * constructor
	 * 
	 * @param name
	 *            name of itinerary
	 * @param segments
	 *            segments of itinerary
	 */
	public Itinerary(String name, List<TripSegment> segments) {
		this.name = name;
		this.segments = new ArrayList<>(segments);
	}

	public Itinerary(String name) {
		this.name = name;
		this.segments = new ArrayList<>();
	}

	/**
	 * get segments
	 * 
	 * @return segments
	 */
	protected List<TripSegment> getTripSegments() {
		return this.segments;
	}

	/**
	 * get name
	 * 
	 * @return name
	 */
	protected String getName() {
		return this.name;
	}

	/**
	 * get the start time of Itinerary
	 * 
	 * @return start time of Itinerary if Itinerary has at least one segment
	 */
	public int getStartTime() {
		int start = Integer.MAX_VALUE;
		for (TripSegment segment : segments) {
			if (segment.getStartTime() < start)
				start = segment.getStartTime();
		}
		return start;
	}

	/**
	 * get the end time of Itinerary
	 * 
	 * @return end time of Itinerary if Itinerary has at least one segment
	 */
	public int getEndTime() {
		int end = 0;
		for (TripSegment segment : segments) {
			if (segment.getEndTime() > end)
				end = segment.getEndTime();
		}
		return end;
	}

	/**
	 * get wait time during the itinerary
	 * 
	 * @return wait time during the itinerary
	 */
	public int getWaitTime() {
		int waitTime = 0;
		for (TripSegment segment : segments) {
			if (segment instanceof WaitSegment) {
				waitTime += segment.getDuration();
			}
		}
		return waitTime;
	}

	/**
	 * get the start location of the itinerary
	 * 
	 * @return start location of the itinerary
	 */
	public Stop getStartLocation() {
		int startTime = getStartTime();
		Stop stop = null;
		for (TripSegment segment : segments) {
			if (segment.getStartTime() == startTime) {
				stop = segment.getStartLocation();
				break;
			}
		}
		return stop;
	}

	/**
	 * get the end location if the itinerary
	 * 
	 * @return end location if the itinerary
	 */
	public Stop getEndLocation() {
		int endTime = getEndTime();
		Stop stop = null;
		for (TripSegment segment : segments) {
			if (segment.getEndTime() == endTime) {
				stop = segment.getEndLocation();
				break;
			}
		}
		return stop;
	}

	/**
	 * get string representation of whole itinerary
	 * the steps are ranked in time order
	 * 
	 * @return string representation of whole itinerary
	 */
	public String getInstructions() {
		
		// build instructions
		StringBuilder stringBuilder = new StringBuilder();
		for (TripSegment segment : segments) {
			// 
			if(segment instanceof WaitSegment)
				stringBuilder.append(
						String.format("Wait at %s (%d-%d)\n", 
						segment.getStartLocation().getName(), 
						segment.getStartTime(), segment.getEndTime())
						);
			else
				stringBuilder.append(
						String.format("Take bus from %s to %s (%d-%d)\n", 
						segment.getStartLocation().getName(), 
						segment.getEndLocation().getName(), 
						segment.getStartTime(), segment.getEndTime())
						);
		}
		
		return stringBuilder.toString();
	}
}

class ItineraryBuilder extends Itinerary {

	/**
	 * constructor
	 * 
	 * @param name
	 *            name of itinerary
	 * @param segments
	 *            segments of itinerary
	 */
	ItineraryBuilder(String name, List<TripSegment> segments) {
		super(name, segments);
	}

	/**
	 * constructor
	 * 
	 * @param name
	 *            name of itinerary
	 */
	ItineraryBuilder(String name) {
		super(name);
	}

	/**
	 * add a segment
	 * 
	 * @param segment
	 *            segment to add
	 */
	void add(TripSegment segment) {
		this.getTripSegments().add(segment);
	}

	/**
	 * add a segment according to two given stoptime
	 * 
	 * @param lastStopTime
	 *            last StopTime
	 * @param nextStopTime
	 *            next StopTime, nextStopTime.time > lastStopTIme
	 */
	void add(StopTime lastStopTime, StopTime nextStopTime) {
		// a waitSegment
		if (lastStopTime.stopEquals(nextStopTime)) {
			TripSegment segment = new WaitSegment(lastStopTime, lastStopTime.getTime(), nextStopTime.getTime());
			add(segment);
		}
		// a busSegment
		else {
			TripSegment segment = new BusSegment(lastStopTime, nextStopTime, lastStopTime.getTime(),
					nextStopTime.getTime());
			add(segment);
		}
	}

	/**
	 * remove a segment
	 * 
	 * @param segment
	 *            segment to remove
	 */
	void remove(TripSegment segment) {
		getTripSegments().remove(segment);
	}

	Itinerary build() {
		Itinerary itinerary = new Itinerary(getName(), new ArrayList<>(getTripSegments()));
		// sort segments
		itinerary.getTripSegments().sort(new Comparator<TripSegment>() {
			@Override
			public int compare(TripSegment segment1, TripSegment segment2) {
				return segment1.getStartTime() - segment2.getStartTime();
			}
		});
		return itinerary;
	}
}
