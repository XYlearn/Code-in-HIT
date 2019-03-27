import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class RoutePlannerBuilderTest {
	
	/**
	 * Testing Strategies
	 * parse from file with empty lines
	 */

	@Test
	public void testBuildRoutePlanner() throws IOException {
		File file = new File("test/P3/test.txt");
		RoutePlanner planner = RoutePlannerBuilder.buildRoutePlanner(file);
		List<Stop> stops = planner.findStopsBySubstring("1");
		assertTrue(stops.size() == 1);
		assertEquals(stops.get(0).getName(), "Vertex1");
		assertTrue(planner.findStopsBySubstring("HIT").isEmpty());
	}

}
