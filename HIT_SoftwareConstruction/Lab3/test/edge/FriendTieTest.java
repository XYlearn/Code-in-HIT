package edge;

import java.util.List;

import org.junit.Before;

import vertex.Vertex;

public class FriendTieTest extends TieTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected FriendTie edgeInstance(String label, List<Vertex> vertices, double weight) {
		return FriendTie.wrap(label, vertices, weight);
	}
	
	public void testWrap() {
		
	}

}
