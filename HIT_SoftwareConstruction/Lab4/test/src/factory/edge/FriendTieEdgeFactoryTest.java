package factory.edge;

import org.junit.Before;

public class FriendTieEdgeFactoryTest extends TieEdgeFactoryTest {

	@Before
	public void setUp() throws Exception {
		this.factory = new FriendTieEdgeFactory();
	}
}
