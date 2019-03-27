/**
 * 
 */
package vertex;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author XHWhy
 *
 */
public class IpVertexTest extends VertexTest {

	/**
	 * Testing Strategies
	 * 
	 * for fillVertexInfo: covers valid IP and invalid IP argument and empty
	 * argument array
	 * 
	 * for clone: test if the cloned Vertex equal to the original vertex
	 * 
	 * for getIp: test if the information returned is correct
	 * 
	 * for validIp: test different format of input and different number range.
	 * 
	 * for wrap: test for valid and invalid arguments.
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see vertex.VertexTest#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		vertex = vertexInstance("1", new String[] { "123.207.159.156" });
		vertex1 = vertexInstance("1", new String[] { "123.207.159.156" });
		vertex2 = vertexInstance("2", new String[] { "123.208.159.156" });
	}

	@Override
	protected IpVertex vertexInstance(String info, String[] args) {
		return Computer.wrap(info, args);
	}

	/**
	 * Test method for {@link vertex.IpVertex#fillVertexInfo(java.lang.String[])}.
	 * covers valid IP and invalid IP argument and empty argument array
	 */
	@Test
	public void testFillVertexInfo() {
		IpVertex vertex = new IpVertex("Vert");
		vertex.fillVertexInfo(new String[] { "123.207.159.156" });
		assertTrue(vertex.getIp().equals("123.207.159.156"));
		try {
			vertex.fillVertexInfo(new String[] { "Wrong format" });
			assert false;
		} catch (IllegalArgumentException e) {
			assertTrue(vertex.getIp().equals("123.207.159.156"));
		}
	}

	/**
	 * Test method for {@link vertex.IpVertex#clone()}. test if the cloned Vertex
	 * equal to the original vertex
	 */
	@Test
	public void testClone() {
		IpVertex vert = (IpVertex) vertex.clone();
		assertEquals(vert.getLabel(), ((IpVertex) vertex).getLabel());
		assertEquals(vert.getIp(), ((IpVertex) vertex).getIp());
	}

	/**
	 * Test method for {@link vertex.IpVertex#getIp()}. test if the information
	 * returned is correct
	 */
	@Test
	public void testGetIp() {
		assertEquals(((IpVertex) vertex).getIp(), "123.207.159.156");
	}

	/**
	 * Test method for {@link vertex.IpVertex#validIp(java.lang.String)}. test
	 * different format of input and different number range.
	 */
	@Test
	public void testValidIp() {
		assertTrue(IpVertex.validIp("123.207.159.156"));
		assertFalse(IpVertex.validIp("123.207.159."));
		assertFalse(IpVertex.validIp("123.207.159.256"));
	}

	/**
	 * Test method for
	 * {@link vertex.IpVertex#wrap(java.lang.Class, java.lang.String, java.lang.String[])}.
	 * test for valid and invalid arguments.
	 */
	@Test
	public void testWrapClassOfQextendsIpVertexStringStringArray() {
		assertTrue(null == IpVertex.wrap(Router.class, "123", new String[] {}));
		assertTrue(null == IpVertex.wrap(Computer.class, "123", new String[] { "123" }));
		assertTrue(IpVertex.wrap(Server.class, "123", new String[] { "123.207.159.156" }) instanceof Server);
	}

	/**
	 * Test method for
	 * {@link vertex.IpVertex#wrap(java.lang.Class, java.lang.String, java.lang.String)}.
	 * test for valid and invalid arguments.
	 */
	@Test
	public void testWrapClassOfQextendsIpVertexStringString() {
		assertTrue(null == IpVertex.wrap(Computer.class, "123", "Wrong Arg"));
		assertTrue(IpVertex.wrap(Server.class, "123", "123.207.159.156") instanceof Server);
	}

}
