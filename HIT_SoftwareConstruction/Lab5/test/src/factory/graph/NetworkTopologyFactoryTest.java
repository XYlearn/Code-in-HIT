/**
 * 
 */
package factory.graph;

import org.junit.Before;

/**
 * @author XHWhy
 *
 */
public class NetworkTopologyFactoryTest extends ConcreteGraphFactoryTest {

	/* (non-Javadoc)
	 * @see factory.graph.ConcreteGraphFactoryTest#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		this.factory = new NetworkTopologyFactory();
	}

}
