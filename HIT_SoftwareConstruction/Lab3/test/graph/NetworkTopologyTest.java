/**
 * 
 */
package graph;

import org.junit.Before;

/**
 * @author XHWhy
 *
 */
public class NetworkTopologyTest extends ConcreteGraphTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected NetworkTopology emptyInstance(String name) {
		return new NetworkTopology(name);
	}

}
