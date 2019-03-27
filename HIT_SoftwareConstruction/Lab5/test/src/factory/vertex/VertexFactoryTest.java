package factory.vertex;

import org.junit.Before;
import org.junit.Test;

public abstract class VertexFactoryTest {

	protected VertexFactory factory;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	abstract public void testCreateVertex();

}
