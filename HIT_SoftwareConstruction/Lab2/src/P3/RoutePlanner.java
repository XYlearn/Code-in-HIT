import java.util.List;

public interface RoutePlanner {
	/**
	 * search stop whoes name contains given string
	 * 
	 * @param search
	 *            substring to search
	 * @return a list of all stops whoes name contains the provides substring search
	 */
	public List<Stop> findStopsBySubstring(String search);

	/**
	 * compute itinerary from src to dst from time with minimize arrival time
	 * 
	 * @param src
	 *            departure place
	 * @param dst
	 *            destination place
	 * @param time
	 *            start time of transit
	 * @return Returns an Itinerary object describing a trip that minimizes the
	 *         arrival time at dest. The itinerary is composed of trip segments as
	 *         described in the UML diagram. If there exists more than one path with
	 *         the same arrival time, any such path may be returned.
	 */
	public Itinerary computeRoute(Stop src, Stop dst, int time);
}
