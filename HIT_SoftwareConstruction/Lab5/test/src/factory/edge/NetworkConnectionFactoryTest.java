package factory.edge;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import edge.NetworkConnection;
import vertex.Computer;
import vertex.Server;

public class NetworkConnectionFactoryTest extends EdgeFactoryTest {

	@Before
	public void setUp() throws Exception {
		this.factory = new NetworkConnectionFactory();
	}

	@Override
	@Test
	public void testCreateEdge() {
		NetworkConnection edge = (NetworkConnection) factory.createEdge("123",
				Arrays.asList(Computer.wrap("com", "172.16.5.24"), Server.wrap("srv", "1.1.1.1")), 100);
		assertTrue(edge != null);
		assertTrue(edge.getLabel().equals("123"));
		assertTrue(edge.getWeight() == 100);
		assertTrue(edge.containVertex(Computer.wrap("com", "172.16.5.24")));
		assertTrue(edge.containVertex(Server.wrap("srv", "1.1.1.1")));

		edge = (NetworkConnection) factory.createEdge("123",
				Arrays.asList(Computer.wrap("com", "172.16.5.24"), Computer.wrap("com", "172.16.5.24")), 100);
		assertTrue(edge == null);
	}

}
