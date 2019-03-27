package vertex;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class VertexTest {

	/**
	 * Testing Strategies
	 * 
	 * for hashCode: test two Vertex with same label
	 * 
	 * for equals: test Vertex with same label and not same label
	 * 
	 * for clone: test the equality between cloned Vertex and the original Vertex
	 */
	Vertex vertex;
	Vertex vertex1;
	Vertex vertex2;

	@Before
	public void setUp() throws Exception {
		vertex = vertexInstance("1", new String[0]);
		vertex1 = vertexInstance("1", new String[0]);
		vertex2 = vertexInstance("2", new String[0]);
	}

	protected Vertex vertexInstance(String label, String[] args) {
		return Word.wrap(label);
	}
	
	@Test
	public void testHashCode() {
		assertEquals(vertex.hashCode(), vertex1.hashCode());
	}

	@Test
	public void testEqualsObject() {
		assertTrue(vertex.equals(vertex));
		assertTrue(vertex.equals(vertex1));
		assertFalse(vertex.equals(vertex2));
	}

	@Test
	public void testClone() {
		vertex.getLabel().equals(vertex.clone().getLabel());
	}

}
