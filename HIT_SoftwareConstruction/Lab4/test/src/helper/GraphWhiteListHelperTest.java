/**
 * 
 */
package helper;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import edge.Edge;
import edge.WordNeighborhood;
import graph.ConcreteGraph;
import graph.GraphPoet;
import graph.MovieGraph;
import helper.GraphWhiteListHelper;
import vertex.Vertex;
import vertex.Word;

/**
 * @author XHWhy
 *
 */
public class GraphWhiteListHelperTest {
	
	/**
	 * Testing Strategies:
	 * 
	 * for {@link helper.GraphWhiteListHelper#isEdgeAdoptable(edge.Edge, graph.Graph)} 
	 */

	protected Vertex v1;
	protected Vertex v2;
	protected Edge wordNeighborhood;
	protected ConcreteGraph graphPoet = new GraphPoet("g");
	protected ConcreteGraph movieGraph = new MovieGraph("g");
	protected ConcreteGraph concreteGraph = new ConcreteGraph("g");
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		v1 = Word.wrap("w1");
		v2 = Word.wrap("v2");
		wordNeighborhood = WordNeighborhood.wrap("we", Arrays.asList(v1, v2), 1);
	}

	/**
	 * Test method for {@link helper.GraphWhiteListHelper#isEdgeAdoptable(edge.Edge, graph.Graph)}.
	 */
	@Test
	public void testIsEdgeAdoptableEdgeGraphOfVertexEdge() {
		assertTrue(GraphWhiteListHelper.isEdgeAdoptable(wordNeighborhood, graphPoet));
		assertFalse(GraphWhiteListHelper.isEdgeAdoptable(wordNeighborhood, movieGraph));
		assertTrue(GraphWhiteListHelper.isEdgeAdoptable(wordNeighborhood, concreteGraph));
	}

	/**
	 * Test method for {@link helper.GraphWhiteListHelper#isVertexAdoptable(vertex.Vertex, graph.Graph)}.
	 */
	@Test
	public void testIsVertexAdoptableVertexGraphOfVertexEdge() {
		assertTrue(GraphWhiteListHelper.isVertexAdoptable(v1, graphPoet));
		assertFalse(GraphWhiteListHelper.isVertexAdoptable(v2, movieGraph));
		assertTrue(GraphWhiteListHelper.isVertexAdoptable(v1, concreteGraph));
	}

}
