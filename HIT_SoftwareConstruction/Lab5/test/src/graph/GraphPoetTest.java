/**
 * 
 */
package graph;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import vertex.Word;

/**
 * @author XHWhy
 *
 */
public class GraphPoetTest extends ConcreteGraphTest {

	/**
	 * Testing Strategies
	 * 
	 * for GraphPoet: test for different input file
	 * 
	 * for poem: has no two-edge-long path between given words has two-edge-long
	 * path between given words
	 * 
	 * for increaseWeight: test for increase edge that not exists and that exists
	 * 
	 * for getBridge: test for bridge exists and not exists
	 */

	@Override
	protected GraphPoet emptyInstance(String name) {
		return new GraphPoet(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see graph.ConcreteGraphTest#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		v1 = vertexInstance("word1", new String[] {});
		v2 = vertexInstance("word2", new String[] {});
	}

	/**
	 * Test method for {@link graph.GraphPoet#GraphPoet(java.lang.String)}. test one
	 * file
	 */
	@Test
	public void testGraphPoet() throws IOException {
		GraphPoet graph = new GraphPoet("graph", "test/src/graph/poem1.txt");
		//System.out.println(graph.vertices());
		assertTrue(graph.vertices().size() == 7);
		assertTrue(graph.targets(Word.wrap("the")).size() == 2);
	}

	/**
	 * Test method for {@link graph.GraphPoet#poem(java.lang.String)}.
	 * has no two-edge-long path between given words has two-edge-long
	 * path between given words
	 */
	@Test
	public void testPoem() throws IOException {
		// covers no bridge
		GraphPoet graphPoet = new GraphPoet("graph", "test/src/graph/poem1.txt");
		assertEquals("expected original text", graphPoet.poem("PHP best language"), 
				"PHP best language");

		// covers one bridge
		//System.out.println(graphPoet.poem("PHP The best language in world!"));
		assertEquals("expected original text", graphPoet.poem("PHP The best language in world!"),
				"PHP is The best language in the world!");

		// covers multiple bridge
		graphPoet = new GraphPoet("graph", "test/src/graph/poem2.txt");
		assertEquals("expected text with inserted bridge", 
				graphPoet.poem("Always for me"), "Always there for me");
	}

	/**
	 * Test method for
	 * {@link graph.GraphPoet#increaseWeight(java.lang.String, java.lang.String)}.
	 * test for increase edge that not exists and that exists
	 */
	@Test
	public void testIncreaseWeight() {
		GraphPoet graph = (GraphPoet)this.graph;
		assertFalse(graph.increaseWeight("word1", "word2"));
		Word w1 = (Word)v1;
		graph.addVertex(w1);
		assertFalse(graph.increaseWeight("word1", "word2"));
		Word w2 = (Word)v2;
		graph.addVertex(w2);
		assertTrue(graph.increaseWeight("word1", "word2"));
		assertTrue(graph.targets(w1).values().iterator().next().get(0) == 1);
	}

	/**
	 * Test method for
	 * {@link graph.GraphPoet#getBridge(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetBridge() throws IOException {
		GraphPoet graphPoet = new GraphPoet("graph", "test/src/graph/poem2.txt");
		assertTrue(graphPoet.getBridge("always", "me").isEmpty());
		assertEquals(graphPoet.getBridge("always", "for"), "there");
	}

}
