package factory.vertex;

import org.junit.Before;

public class ServerVertexFactoryTest extends IpVertexFactoryTest {

	@Before
	public void setUp() throws Exception {
		this.factory = new ServerVertexFactory();
	}
}
