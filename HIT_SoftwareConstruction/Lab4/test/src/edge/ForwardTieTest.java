package edge;

import java.util.List;

import org.junit.Before;

import vertex.Vertex;

public class ForwardTieTest extends TieTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected ForwardTie edgeInstance(String label, List<Vertex> vertices, double weight) {
		return ForwardTie.wrap(label, vertices, weight);
	}
	
	@Override
	public void testWrap() {
		
	}

}
