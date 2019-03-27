package factory.graph;

import org.junit.Before;

public class SocialNetworkFactoryTest extends ConcreteGraphFactoryTest {

	@Before
	public void setUp() throws Exception {
		this.factory = new SocialNetworkFactory();
	}

}
