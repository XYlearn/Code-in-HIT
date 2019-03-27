
package helper;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import exception.CommandException;
import factory.vertex.ComputerVertexFactory;
import graph.ConcreteGraph;
import vertex.Vertex;
import helper.centrality.*;

public class GraphMetricsTest {

	/**
	 * Testing Strategies:
	 * 
	 * for {@link helper.GraphMetrics#degreeCentrality(graph.Graph, vertex.Vertex)}.
	 * test vertex has degree 0, 1 and more than 1. test vertex in graph and not in
	 * graph
	 * 
	 * for {@link helper.GraphMetrics#degreeCentrality(graph.Graph)}. test empty
	 * graph and non-empty graph
	 * 
	 * for
	 * {@link helper.GraphMetrics#closenessCentrality(graph.Graph, vertex.Vertex)}.
	 * test empty graph and non-empty graph. test vertex in graph and not in graph
	 * 
	 * for
	 * {@link helper.GraphMetrics#betweennessCentrality(graph.Graph, vertex.Vertex)}.
	 * test result is 0, 1 and more than 1. test vertex in graph and not in graph
	 * 
	 * for
	 * {@link helper.GraphMetrics#inDegreeCentrality(graph.Graph, vertex.Vertex)}.
	 * test vertex has inDegree 0, 1 and more than 1. test vertex in graph and not
	 * in graph
	 * 
	 * for
	 * {@link helper.GraphMetrics#outDegreeCentrality(graph.Graph, vertex.Vertex)}.test
	 * vertex has outDegree 0, 1 and more than 1. test vertex in graph and not in
	 * graph
	 * 
	 * for
	 * {@link helper.GraphMetrics#distance(graph.Graph, vertex.Vertex, vertex.Vertex)}.
	 * test src and dst are equals and not equals. test vertex in graph and not in
	 * graph. test vertex connected and not connected
	 * 
	 * for {@link helper.GraphMetrics#eccentricity(graph.Graph, vertex.Vertex)}.
	 * test graph is empty and non-empty. test vertex in graph and not in graph
	 * 
	 * for {@link helper.GraphMetrics#radius(graph.Graph)}. test graph is empty and
	 * non-empty
	 * 
	 * for {@link helper.GraphMetrics#diameter(graph.Graph)}. test graph is empty
	 * and non-empty
	 */

	protected ConcreteGraph graph1;
	protected ConcreteGraph graph2;
	protected ConcreteGraph graph3;
	protected ConcreteGraph graph4;
	protected ConcreteGraph graph5;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		try {
			graph1 = ParseCommandHelper.buildGraphFromFile("test/src/helper/testcase.txt");
			graph2 = ParseCommandHelper.buildGraphFromFile("test/src/helper/testcase2.txt");
			graph3 = ParseCommandHelper.buildGraphFromFile("test/src/helper/testcase3.txt");
			graph4 = ParseCommandHelper.buildGraphFromFile("test/src/helper/testcase4.txt");
			graph5 = ParseCommandHelper.buildGraphFromFile("test/src/helper/testcase5.txt");
		} catch (CommandException e) {
			System.err.println(e.getCommand());
			System.err.println(e.getMessage());
			System.err.println(e.getStartPos());
		}
	}

	/**
	 * Test method for
	 * {@link helper.GraphMetrics#degreeCentrality(graph.Graph, vertex.Vertex)}.test
	 * vertex has degree 0, 1 and more than 1. test vertex in graph and not in graph
	 */
	@Test
	public void testDegreeCentralityGraphOfVertexEdgeVertex() {
		assertTrue(GraphMetrics.degreeCentrality(graph1, Vertex.common("Server1")) == 2);
		assertTrue(GraphMetrics.degreeCentrality(graph1, Vertex.common("Computer1")) == 1);
		graph1.addVertex(new ComputerVertexFactory().createVertex("Computer2", new String[] { "123.123.123.123" }));
		assertTrue(GraphMetrics.degreeCentrality(graph1, Vertex.common("Computer2")) == 0);
		assertTrue(GraphMetrics.degreeCentrality(graph1, Vertex.common("Computer3")) < 0);

	}

	/**
	 * Test method for
	 * {@link helper.GraphMetrics#degreeCentrality(graph.Graph)}.test empty graph
	 * and non-empty graph
	 */
	@Test
	public void testDegreeCentralityGraphOfVertexEdge() {
		assertTrue(GraphMetrics.degreeCentrality(graph1) == 1);
		graph1.addVertex(new ComputerVertexFactory().createVertex("Computer2", new String[] { "123.123.123.123" }));
		assertTrue(GraphMetrics.degreeCentrality(graph1) == 2.0 / 3);
	}

	/**
	 * Test method for
	 * {@link helper.GraphMetrics#closenessCentrality(graph.Graph, vertex.Vertex)}.test
	 * empty graph and non-empty graph. test vertex in graph and not in graph
	 */
	@Test
	public void testClosenessCentrality() {
		assertTrue(GraphMetrics.closenessCentrality(graph2, Vertex.common("TheGreenMile")) == 0.5);
		assertTrue(GraphMetrics.closenessCentrality(graph2, Vertex.common("Tom")) < 0);
	}

	/**
	 * Test method for
	 * {@link helper.GraphMetrics#betweennessCentrality(graph.Graph, vertex.Vertex)}.
	 * test result is 0, 1 and more than 1. test vertex in graph and not in graph
	 */
	@Test
	public void testBetweennessCentrality() {
		assertTrue(GraphMetrics.betweennessCentrality(graph2, Vertex.common("TheGreenMile")) == 4);
		assertTrue(GraphMetrics.betweennessCentrality(graph2, Vertex.common("TheShawshankRedemption")) == 6);
		assertTrue(GraphMetrics.betweennessCentrality(graph2, Vertex.common("No")) < 0);
	}

	/**
	 * Test method for
	 * {@link helper.GraphMetrics#inDegreeCentrality(graph.Graph, vertex.Vertex)}.
	 * test vertex has inDegree 0, 1 and more than 1. test vertex in graph and not
	 * in graph
	 */
	@Test
	public void testInDegreeCentrality() {
		assertTrue(GraphMetrics.inDegreeCentrality(graph3, Vertex.common("w3")) == 3);
		assertTrue(GraphMetrics.inDegreeCentrality(graph3, Vertex.common("w4")) == 2);
		assertTrue(GraphMetrics.inDegreeCentrality(graph3, Vertex.common("w2")) == 1);
		assertTrue(GraphMetrics.inDegreeCentrality(graph3, Vertex.common("w5")) == 0);
		assertTrue(GraphMetrics.inDegreeCentrality(graph3, Vertex.common("www")) < 0);
	}

	/**
	 * Test method for
	 * {@link helper.GraphMetrics#outDegreeCentrality(graph.Graph, vertex.Vertex)}.
	 * test vertex has outDegree 0, 1 and more than 1. test vertex in graph and not
	 * in graph
	 */
	@Test
	public void testOutDegreeCentrality() {
		assertTrue(GraphMetrics.outDegreeCentrality(graph3, Vertex.common("w1")) == 3);
		assertTrue(GraphMetrics.outDegreeCentrality(graph3, Vertex.common("w2")) == 1);
		assertTrue(GraphMetrics.outDegreeCentrality(graph3, Vertex.common("w5")) == 0);
		assertTrue(GraphMetrics.outDegreeCentrality(graph3, Vertex.common("www")) < 0);
	}

	/**
	 * Test method for
	 * {@link helper.GraphMetrics#distance(graph.Graph, vertex.Vertex, vertex.Vertex)}.
	 * test src and dst are equals and not equals. test vertex in graph and not in
	 * graph. test vertex connected and not connected
	 */
	@Test
	public void testDistance() {
		assertTrue(GraphMetrics.distance(graph4, Vertex.common("w1"), Vertex.common("w1")) == 0);
		assertTrue(GraphMetrics.distance(graph4, Vertex.common("w1"), Vertex.common("w3")) == 2);
		assertTrue(GraphMetrics.distance(graph4, Vertex.common("w1"), Vertex.common("www")) == -1);
		assertTrue(GraphMetrics.distance(graph4, Vertex.common("w1"), Vertex.common("w5")) == -2);
	}

	/**
	 * Test method for
	 * {@link helper.GraphMetrics#eccentricity(graph.Graph, vertex.Vertex)}. test
	 * graph is empty and non-empty. test vertex in graph and not in graph
	 */
	@Test
	public void testEccentricity() {
		assertTrue(GraphMetrics.eccentricity(graph3, Vertex.common("w1")) == -2);
		assertTrue(GraphMetrics.eccentricity(graph5, Vertex.common("w1")) == 1);
		assertTrue(GraphMetrics.eccentricity(graph5, Vertex.common("w2")) == 3);
		assertTrue(GraphMetrics.eccentricity(graph5, Vertex.common("w10")) < 0);
	}

	/**
	 * Test method for {@link helper.GraphMetrics#radius(graph.Graph)}. test graph
	 * is empty and non-empty
	 */
	@Test
	public void testRadius() {
		assertTrue(GraphMetrics.radius(graph5) == 1);
		assertTrue(GraphMetrics.radius(graph4) < 0);
		assertTrue(GraphMetrics.radius(new ConcreteGraph("test")) == -1);
	}

	/**
	 * Test method for {@link helper.GraphMetrics#diameter(graph.Graph)}. test graph
	 * is empty and non-empty
	 */
	@Test
	public void testDiameter() {
		assertTrue(GraphMetrics.diameter(graph5) == 3);
		assertTrue(GraphMetrics.diameter(graph4) == 0);
		assertTrue(GraphMetrics.diameter(new ConcreteGraph("test")) == -1);
	}

	@Test
	public void testCentrality() {
		ClosenessCentralityStrategy closenessCentrality = new ClosenessCentralityStrategy();
		BetweennessCentralityStrategy betweennessCentrality = new BetweennessCentralityStrategy();
		DegreeCentralityStrategy degreeCentrality = new DegreeCentralityStrategy();
		GraphMetrics metrics = new GraphMetrics(degreeCentrality);
		assertTrue(metrics.calculateCentrality(graph1, Vertex.common("Server1")) == 2);
		metrics.setCentrality(closenessCentrality);
		assertTrue(metrics.calculateCentrality(graph2, Vertex.common("TheGreenMile")) == 0.5);
		metrics.setCentrality(betweennessCentrality);
		assertTrue(metrics.calculateCentrality(graph2, Vertex.common("TheShawshankRedemption")) == 6);

	}
}
