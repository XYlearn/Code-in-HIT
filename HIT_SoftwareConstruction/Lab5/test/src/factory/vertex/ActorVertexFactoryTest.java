package factory.vertex;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import vertex.Actor;
import vertex.Vertex;

public class ActorVertexFactoryTest extends PersonVertexFactoryTest {

	@Before
	public void setUp() throws Exception {
		this.factory = new ActorVertexFactory();
	}

	@Test
	public void test() {
		Vertex vertex = factory.createVertex("actor", new String[] {"F", "23"});
		assertTrue(vertex instanceof Actor);
	}

}
