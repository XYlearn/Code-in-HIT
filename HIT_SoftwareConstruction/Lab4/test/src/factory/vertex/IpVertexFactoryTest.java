package factory.vertex;

import static org.junit.Assert.*;

import org.junit.Before;

import vertex.IpVertex;

public abstract class IpVertexFactoryTest extends VertexFactoryTest {

	@Before
	public void setUp() throws Exception {
	}

	@Override
	public void testCreateVertex() {
		IpVertex vertex = (IpVertex) factory.createVertex("vertex", new String[] {"192.168.32.1"});
		assertTrue(vertex != null);
		assertTrue(vertex.getLabel().equals("vertex"));
		assertTrue(vertex.getIp().equals("192.168.32.1"));
		
		vertex = (IpVertex) factory.createVertex("vertex", new String[] {});
		assertTrue(vertex == null);
		
		vertex = (IpVertex) factory.createVertex("vertex", new String[] {"321.168.32.1"});
		assertTrue(vertex == null);
		
		vertex = (IpVertex) factory.createVertex("vertex", new String[] {"192.167.4.2.1"});
		assertTrue(vertex == null);
	}

}
