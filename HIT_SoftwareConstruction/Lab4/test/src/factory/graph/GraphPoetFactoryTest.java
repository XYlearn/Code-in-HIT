/**
 * 
 */
package factory.graph;

import org.junit.Before;

/**
 * @author XHWhy
 *
 */
public class GraphPoetFactoryTest extends ConcreteGraphFactoryTest {

	/* (non-Javadoc)
	 * @see factory.graph.ConcreteGraphFactoryTest#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		this.factory = new GraphPoetFactory();
	}

}
