package vertex;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class WordTest extends VertexTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testWrapString() {
		assertTrue(null != Word.wrap("1"));
		assertTrue(Word.wrap("1").getLabel().equals("1"));
	}

	@Test
	public void testWrapStringStringArray() {
		assertTrue(null != Word.wrap("1", new String[0]));
		assertTrue(Word.wrap("1", new String[0]).getLabel().equals("1"));
	}

}
