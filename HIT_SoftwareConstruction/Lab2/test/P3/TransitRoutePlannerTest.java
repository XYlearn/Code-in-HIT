import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class TransitRoutePlannerTest {
	
	/**
	 * Testing Strategies
	 * 
	 * for findStopBySubString
	 * covers empty string, nonempty string
	 * 		full name, part name
	 * 
	 * for computeRoute
	 * covers don't need to wait, need to wait
	 * 		wait time > 1200, wait time <= 1200
	 * 		not be able to access and be able to access
	 */

	/**
	 * Testing {@link TransitRoutePlanner#findStopsBySubstring(String)}
	 * covers empty string, nonempty string
	 * 		full name, part name
	 */
	@Test
	public void testFindStopsBySubstring() {
		File file = new File("rsrc/test.txt");
		RoutePlanner planner = null;
		try {
			planner = RoutePlannerBuilder.buildRoutePlanner(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<Stop> stops = planner.findStopsBySubstring("Vertex1");
		assertTrue(stops.size() == 1);
		assertEquals(stops.get(0).getName(), "Vertex1");
		
		stops = planner.findStopsBySubstring("2");
		assertTrue(stops.size() == 1);
		assertEquals(stops.get(0).getName(), "Vertex2");
		
		stops = planner.findStopsBySubstring("");
		assertTrue(stops.size() == 6);
	}

	/**
	 * Testing {@link TransitRoutePlanner#computeRoute(Stop, Stop, int)}
	 * covers don't need to wait, need to wait
	 * 		wait time > 1200, wait time <= 1200
	 * 		not be able to access and be able to access
	 */
	@Test
	public void testComputeRoute() {
		File file = new File("rsrc/test.txt");
		RoutePlanner planner = null;
		try {
			planner = RoutePlannerBuilder.buildRoutePlanner(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Stop src = Stop.getInstance("Vertex1", 1.0, 1.0);
		Stop dst = Stop.getInstance("Vertex4", 4.0, 4.0);
		int time = 0;
		Itinerary itinerary = planner.computeRoute(src, dst, time);
		assertTrue(itinerary.getWaitTime() == 60);
		assertTrue(itinerary.getTripSegments().size() == 3);
		assertTrue(itinerary.getTripSegments().get(0).getEndLocation().getName().equals("Vertex5"));
		assertTrue(itinerary.getTripSegments().get(1).getEndLocation().getName().equals("Vertex5"));
		assertTrue(itinerary.getEndTime() == 160);
		
		time = 50;
		itinerary = planner.computeRoute(src, dst, time);
		assertTrue(itinerary.getWaitTime() == 410);
		assertTrue(itinerary.getTripSegments().size() == 4);
		assertTrue(itinerary.getTripSegments().get(0).getEndLocation().getName().equals("Vertex1"));
		assertTrue(itinerary.getTripSegments().get(1).getEndLocation().getName().equals("Vertex5"));
		assertTrue(itinerary.getTripSegments().get(2).getEndLocation().getName().equals("Vertex5"));
		assertTrue(itinerary.getEndTime() == 560);
		
		time = 1600;
		itinerary = planner.computeRoute(src, dst, time);
		assertTrue(itinerary.getTripSegments().size() == 0);
	}

}
