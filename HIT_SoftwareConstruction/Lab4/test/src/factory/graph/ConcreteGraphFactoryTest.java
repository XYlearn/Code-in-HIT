/**
 * 
 */
package factory.graph;

import static org.junit.Assert.*;

import org.junit.Before;

import edge.Edge;
import graph.Graph;
import vertex.Vertex;

/**
 * @author XHWhy
 *
 */
public class ConcreteGraphFactoryTest extends GraphFactoryTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.factory = new ConcreteGraphFactory();
	}

	@Override
	public void testCreateGraph() {
		Graph<Vertex, Edge> graph = factory.createGraph("test");
		assertTrue(graph != null);
		assertTrue(graph.vertices().isEmpty());
		assertTrue(graph.edges().isEmpty());
		assertTrue(graph.getName().equals("test"));
		assertTrue(graph.vertexCount() == 0);
		assertTrue(graph.edgeCount() == 0);
	}

}
