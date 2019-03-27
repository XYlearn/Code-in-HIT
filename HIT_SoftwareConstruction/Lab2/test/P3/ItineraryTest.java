import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class ItineraryTest {
	
	/**
	 * Testing Strategies
	 * test each method with a generated Itinerary
	 * beacause the methods are all get methods 
	 */
	
	private Itinerary itinerary;
	private Stop src = Stop.getInstance("Vertex1", 1.0, 1.0);
	private Stop dst = Stop.getInstance("Vertex4", 4.0, 4.0);
	
	public ItineraryTest() {
		File file = new File("rsrc/test.txt");
		RoutePlanner planner = null;
		try {
			planner = RoutePlannerBuilder.buildRoutePlanner(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int time = 0;
		itinerary = planner.computeRoute(src, dst, time);
	}

	@Test
	public void testGetStartTime() {
		assertTrue(itinerary.getStartTime() == 0);
	}

	@Test
	public void testGetEndTime() {
		assertTrue(itinerary.getEndTime() == 160);
	}

	@Test
	public void testGetWaitTime() {
		assertTrue(itinerary.getWaitTime() == 60);
	}

	@Test
	public void testGetStartLocation() {
		assertTrue(Stop.stopEquals(itinerary.getStartLocation(), src));
	}

	@Test
	public void testGetEndLocation() {
		assertTrue(Stop.stopEquals(itinerary.getEndLocation(), dst));
	}
}
