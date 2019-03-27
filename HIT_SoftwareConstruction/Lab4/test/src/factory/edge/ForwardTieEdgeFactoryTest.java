package factory.edge;

import org.junit.Before;

public class ForwardTieEdgeFactoryTest extends TieEdgeFactoryTest {

	@Before
	public void setUp() throws Exception {
		this.factory = new ForwardTieEdgeFactory();
	}

}
