package factory.vertex;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import vertex.Director;
import vertex.Vertex;

public class DirectorVertexFactoryTest extends PersonVertexFactoryTest {

	@Before
	public void setUp() throws Exception {
		this.factory = new DirectorVertexFactory();
	}

	@Test
	public void test() {
		Vertex vertex = factory.createVertex("actor", new String[] { "F", "23" });
		assertTrue(vertex instanceof Director);
	}

}
