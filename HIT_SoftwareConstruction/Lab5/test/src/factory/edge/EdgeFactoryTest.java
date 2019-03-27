/**
 * 
 */
package factory.edge;

import org.junit.Before;
import org.junit.Test;

/**
 * @author XHWhy
 *
 */
public abstract class EdgeFactoryTest {

	protected EdgeFactory factory;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}
	
	/**
	 * Test method for {@link factory.edge.EdgeFactory#createEdge(java.lang.String, java.util.List, double)}.
	 */
	@Test
	abstract public void testCreateEdge();

}
