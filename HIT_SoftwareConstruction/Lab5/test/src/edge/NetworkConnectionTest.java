/**
 * 
 */
package edge;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import vertex.Computer;
import vertex.Router;
import vertex.Server;
import vertex.Vertex;

public class NetworkConnectionTest extends EdgeTest {

	/**
	 * Testing Strategies
	 * 
	 * for addVertices: test add valid vertices and invalid vertices, test add
	 * the first time and the second time
	 * 
	 * for sourceVertices and targetVertices: covers called when the edge is
	 * valid
	 * 
	 * for clone: test whether the returned instance has same field to the
	 * original one, and test for invalid edge
	 * 
	 * for wrap: test valid arguments and invalid arguments
	 */

	protected Vertex v5;
	protected Vertex v6;
	protected Vertex v7;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		v1 = Computer.wrap("1", "123.207.159.156");
		v2 = Server.wrap("2", "123.207.159.157");
		v3 = Router.wrap("3", "123.207.159.1");
		v4 = Server.wrap("2", "123.207.159.157");
		v5 = Computer.wrap("5", "1.2.3.4");
		v6 = Server.wrap("6", "3.4.5.6");
		v7 = Router.wrap("7", "4.12.14.54");
		e1 = edgeInstance("12", Arrays.asList(v1, v2), 0.1);
	}

	@Override
	protected NetworkConnection edgeInstance(String label,
			List<Vertex> vertices, double weight) {
		return NetworkConnection.wrap(label, vertices, weight);
	}

	/**
	 * Test method for
	 * {@link edge.NetworkConnection#addVertices(java.util.List)}. test add
	 * valid vertices and invalid vertices, test add the first time and the
	 * second time
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testAddVertices() {
		NetworkConnection edge = new NetworkConnection("1", 0.3);
		assertTrue(edge.addVertices(Arrays.asList(v1, v2)));
		assertFalse(edge.addVertices(Arrays.asList(v1, v3)));
		edge = new NetworkConnection("1", 0.3);
		assertFalse(edge.addVertices(Arrays.asList(v1)));
		edge = new NetworkConnection("1", 0.3);
		assertFalse(edge.addVertices(Arrays.asList(v1, v1)));
		edge = new NetworkConnection("1", 0.3);
		assertFalse(edge.addVertices(Arrays.asList(v1, v5)));
		assertFalse(edge.addVertices(Arrays.asList(v2, v6)));
		assertTrue(edge.vertices().isEmpty());
		assertTrue(edge.addVertices(Arrays.asList(v3, v7)));
	}

	/**
	 * Test method for {@link edge.NetworkConnection#sourceVertices()}.covers
	 * called when the edge is valid
	 */
	@Test
	public void testSourceVertices() {
		Edge edge = e1;
		assertTrue(edge.sourceVertices().size() == 1);
		assertTrue(edge.sourceVertices().contains(v1));
	}

	/**
	 * Test method for {@link edge.NetworkConnection#targetVertices()}.covers
	 * called when the edge is valid
	 */
	@Test
	public void testTargetVertices() {
		Edge edge = e1;
		assertTrue(edge.targetVertices().size() == 1);
		assertTrue(edge.targetVertices().contains(v2));
	}

	/**
	 * Test method for {@link edge.NetworkConnection#valid()}.
	 */
	@Test
	public void testValid() {
		Edge edge = e1;
		assertTrue(edge.valid());
		edge.removeVertex(v1);
		assertFalse(edge.valid());
		assertTrue(null == edgeInstance("15", Arrays.asList(v1, v5), 4));
		assertTrue(null == edgeInstance("26", Arrays.asList(v2, v6), 4));
		assertTrue(null != edgeInstance("17", Arrays.asList(v1, v7), 32));
	}

	/**
	 * Test method for {@link edge.NetworkConnection#clone()}. test whether the
	 * returned instance has same field to the original one, and test for
	 * invalid edge
	 */
	@Test
	public void testClone() {
		// Edge edge = e1;
		// assertTrue(null != edge.clone());
		// assertTrue(edge.clone().getLabel().equals(edge.getLabel()));
		// assertTrue(edge.clone().getWeight() == edge.getWeight());
		// assertTrue(edge.clone().sourceVertices().containsAll(edge.sourceVertices()));
		// assertTrue(edge.clone().targetVertices().containsAll(edge.targetVertices()));
		// edge.removeVertex(v1);
		// try {
		// edge.clone();
		// assert false;
		// } catch (Exception e) {
		//
		// }
	}

	/**
	 * Test method for
	 * {@link edge.NetworkConnection#wrap(vertex.Vertex, vertex.Vertex, double)}.
	 */
	@Test
	public void testWrap() {
		NetworkConnection edge = NetworkConnection.wrap("12",
				Arrays.asList(v1, v2), 0.5);
		assertTrue(null != edge);
		assertTrue(edge.getWeight() == 0.5);
		assertTrue(edge.targetVertices().contains(v2));
		assertTrue(edge.sourceVertices().contains(v1));

		assertTrue(null == NetworkConnection.wrap("11", Arrays.asList(v1, v1),
				0.5));
		assertTrue(null == NetworkConnection.wrap("12", Arrays.asList(v1, v2),
				-0.5));

		assertTrue(
				null == NetworkConnection.wrap("15", Arrays.asList(v1, v5), 4));
		assertTrue(
				null == NetworkConnection.wrap("26", Arrays.asList(v2, v6), 4));
		assertTrue(null != NetworkConnection.wrap("17", Arrays.asList(v1, v7),
				32));
	}

}
