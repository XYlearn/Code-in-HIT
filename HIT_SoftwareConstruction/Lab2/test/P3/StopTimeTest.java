import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class StopTimeTest {
	
	/**
	 * Testing Strategies
	 * 
	 * For get method:
	 * 	check the returned value
	 * 
	 * For hash and equals:
	 * 	check the equality between self and self, self and Stop 
	 * 	with same fields value, self and Stop with different value
	 */
	
	private static StopTime stopTime1;
	private static StopTime stopTime2;
	private static StopTime stopTime3;
	private static StopTime stopTime4;
	
	@Before
	public void setUp() {
		stopTime1 = new StopTime("HIT", 45.2, 100.5, 65535);
		stopTime2 = new StopTime(Stop.getInstance("HIT", 45.2, 100.5), 65536);
		stopTime3 = new StopTime("CMU", 90.1, 45.2, 65535);
		stopTime4 = new StopTime("HIT", 45.2, 100.5, 65535);
	}
	
	@Test
	public void testGetName() {
		assertEquals(stopTime1.getName(), "HIT");
	}

	@Test
	public void testGetLatitude() {
		assertTrue(stopTime1.getLatitude() == 45.2);
	}

	@Test
	public void testGetLongitude() {
		assertTrue(stopTime1.getLongitude() == 100.5);
	}

	@Test
	public void testGetTime() {
		assertTrue(stopTime1.getTime() == 65535);
	}

	@Test
	public void testStopEquals() {
		assertTrue(stopTime1.stopEquals(stopTime1));
		assertTrue(stopTime1.stopEquals(stopTime2));
		assertFalse(stopTime1.stopEquals(stopTime3));
	}

	@Test
	public void testEqualsObject() {
		assertTrue(stopTime1.equals(stopTime1));
		assertFalse(stopTime1.equals(stopTime2));
		assertFalse(stopTime1.equals(stopTime3));
		assertTrue(stopTime1.equals(stopTime4));
	}
	
	@Test
	public void testHash() {
		assertTrue(stopTime1.hashCode() == stopTime4.hashCode());
		assertTrue(stopTime1.hashCode() == stopTime1.hashCode());
		assertTrue(stopTime1.hashCode() != stopTime2.hashCode());
	}

}
