package vertex;

import static org.junit.Assert.*;

import org.junit.Test;

public class ServerTest extends IpVertexTest {

	@Override
	protected Server vertexInstance(String label, String[] args) {
		return Server.wrap(label, args);
	}

	@Test
	public void testWrapStringStringArray() {
		assertTrue(null == Server.wrap("123", new String[] { "Wrong Arg" }));
		assertTrue(Server.wrap("123", new String[] { "123.207.159.156" }) instanceof Server);
	}

	@Test
	public void testWrapStringString() {
		assertTrue(null == Server.wrap("123", "Wrong Arg"));
		assertTrue(IpVertex.wrap(Server.class, "123", "123.207.159.156") instanceof Server);
	}

}
