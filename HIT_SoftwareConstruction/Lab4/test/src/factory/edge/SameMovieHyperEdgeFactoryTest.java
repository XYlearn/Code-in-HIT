/**
 * 
 */
package factory.edge;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import edge.SameMovieHyperEdge;
import vertex.Actor;

/**
 * @author XHWhy
 *
 */
public class SameMovieHyperEdgeFactoryTest extends EdgeFactoryTest {

	/*
	 * (non-Javadoc)
	 * 
	 * @see factory.edge.EdgeFactoryTest#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		this.factory = new SameMovieHyperEdgeFactory();
	}

	@Override
	@Test
	public void testCreateEdge() {
		SameMovieHyperEdge edge = (SameMovieHyperEdge) factory.createEdge("123",
				Arrays.asList(Actor.wrap("a1", new String[] { "M", "56" }),
						Actor.wrap("a2", new String[] { "F", "17" }), Actor.wrap("a3", new String[] { "F", "32" })),
				-1);
		assertTrue(edge != null);
		assertTrue(edge.getLabel().equals("123"));
		assertTrue(edge.getWeight() == -1);
		assertTrue(edge.containVertex(Actor.wrap("a1", new String[] { "M", "56" })));
		assertTrue(edge.containVertex(Actor.wrap("a2", new String[] { "F", "17" })));
		assertTrue(edge.containVertex(Actor.wrap("a3", new String[] { "F", "32" })));

		edge = (SameMovieHyperEdge) factory.createEdge("123",
				Arrays.asList(Actor.wrap("a3", new String[] { "F", "32" })), -1);
		assertTrue(edge == null);
	}

}
