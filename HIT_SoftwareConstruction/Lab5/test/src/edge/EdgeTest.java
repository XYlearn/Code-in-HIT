package edge;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import vertex.Vertex;
import vertex.Word;

public class EdgeTest {

	/**
	 * Testing Strategies
	 * 
	 * for hashCode: covers edges same label
	 * 
	 * for setWeight: covers valid weight and invalid weight
	 * 
	 * for removeVertex: covers exists vertex and non-exists vertex
	 * 
	 * for containVertex: test for vertex exists and not exists
	 * 
	 * for vertices: test whether the number and vertex match
	 * 
	 * for vSize: test whether the number is correct
	 * 
	 * for sourceVertices and targetVertices: test for the returned size and content
	 * 
	 * for valid: test valid and invalid edge
	 */

	protected Vertex v1;
	protected Vertex v2;
	protected Vertex v3;
	protected Vertex v4;
	protected Edge e1024;
	protected Edge e1025;
	protected Edge e1;

	@Before
	public void setUp() throws Exception {
		v1 = vertexInstance("1", new String[0]);
		v2 = vertexInstance("2", new String[0]);
		v3 = vertexInstance("3", new String[0]);
		v4 = vertexInstance("2", new String[0]);
		e1024 = edgeInstance("12", Arrays.asList(v1, v2), 0.1);
		e1025 = edgeInstance("12", Arrays.asList(v1, v4), 0.1);
		e1 = edgeInstance("12", Arrays.asList(v1, v2), 0.1);
	}

	protected Edge edgeInstance(String label, List<Vertex> vertices, double weight) {
		return WordNeighborhood.wrap(label, vertices, weight);
	}
	
	protected Vertex vertexInstance(String label, String[] args) {
		return Word.wrap(label, args);
	}

	@Test
	public void testHashCode() {
		assertTrue(e1024.hashCode() == e1025.hashCode());
	}

	@Test
	public void testSetWeight() {
		assertTrue(e1024.setWeight(0.3));
		assertFalse(e1024.setWeight(-0.3));
	}

	@Test
	public void testRemoveVertex() {
		Edge edge = e1024;
		assertTrue(edge.removeVertex(v1));
		assertFalse(edge.containVertex(v1));
		assertFalse(edge.removeVertex(v1));
	}

	@Test
	public void testContainVertex() {
		Edge edge = e1024;
		assertTrue(edge.containVertex(v1));
		assertFalse(edge.containVertex(v3));
	}

	@Test
	public void testVertices() {
		Edge edge = e1024;
		assertTrue(edge.vertices().size() == 2);
		assertTrue(edge.vertices().contains(v1));
		assertTrue(edge.vertices().contains(v2));
		edge.vertices().remove(v1);
		assertTrue(edge.vertices().size() == 2);
	}

	@Test
	public void testVSize() {
		Edge edge = e1024;
		assertTrue(edge.vSize() == 2);
	}

	@Test
	public void testSourceVertices() {
		Edge edge = e1024;
		assertTrue(edge.sourceVertices().size() == 1);
		assertTrue(edge.sourceVertices().contains(v1));
	}

	@Test
	public void testTargetVertices() {
		Edge edge = e1024;
		assertTrue(edge.targetVertices().size() == 1);
		assertTrue(edge.targetVertices().contains(v2));
	}

	@Test
	public void testValid() {
		Edge edge = e1024;
		assertTrue(edge.valid());
		edge.removeVertex(v1);
		assertFalse(edge.valid());
		edge.removeVertex(v2);
		assertFalse(edge.valid());
	}

}
