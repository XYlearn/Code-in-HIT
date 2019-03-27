package vertex;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ComputerTest extends IpVertexTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected Computer vertexInstance(String label, String[] args) {
		return Computer.wrap(label, args);
	}

	@Test
	public void testWrapStringStringArray() {
		assertTrue(null == Computer.wrap("123", new String[] { "Wrong Arg" }));
		assertTrue(Computer.wrap("123", new String[] { "123.207.159.156" }) instanceof Computer);
	}

	@Test
	public void testWrapStringString() {
		assertTrue(null == Computer.wrap("123", "Wrong Arg"));
		assertTrue(IpVertex.wrap(Computer.class, "123", "123.207.159.156") instanceof Computer);
	}

}
