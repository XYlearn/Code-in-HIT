package vertex;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class RouterTest extends IpVertexTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected Router vertexInstance(String label, String[] args) {
		return Router.wrap(label, args);
	}

	@Test
	public void testWrapStringStringArray() {
		assertTrue(null == Router.wrap("123", new String[] { "Wrong Arg" }));
		assertTrue(Router.wrap("123", new String[] { "123.207.159.156" }) instanceof Router);
	}

	@Test
	public void testWrapStringString() {
		assertTrue(null == Router.wrap("123", "Wrong Arg"));
		assertTrue(IpVertex.wrap(Router.class, "123", "123.207.159.156") instanceof Router);
	}

}
