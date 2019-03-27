/**
 * 
 */
package factory.graph;

import org.junit.Before;
import org.junit.Test;

import edge.Edge;
import vertex.Vertex;

/**
 * @author XHWhy
 *
 */
public abstract class GraphFactoryTest {

	protected GraphFactory<Vertex, Edge> factory;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}
	
	/**
	 * Test method for {@link factory.graph.GraphFactory#createGraph(java.lang.String)}.
	 */
	@Test
	abstract public void testCreateGraph();
}
