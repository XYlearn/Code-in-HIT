package helper;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.Before;
import org.junit.Test;

import edge.Edge;
import edu.uci.ics.jung.graph.Graph;
import graph.ConcreteGraph;
import util.GraphConverter;
import vertex.Vertex;

public class GraphConverterTest {

	/**
	 * Testing Strategies: test for the equality of Converted Graph and the original
	 * Graph
	 */

	protected ConcreteGraph graph;

	@Before
	public void setUp() throws Exception {
		File file = new File("test/src/helper/testcase.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] cbuf = new char[(int) file.length()];
		reader.read(cbuf);
		reader.close();
		String content = String.valueOf(cbuf);
		graph = ParseCommandHelper.buildGraph(content);
	}

	@Test
	public void testConvert2Jung() {
		Graph<Vertex, Edge> g = GraphConverter.convert2Jung(graph);
		assertTrue(null != g);
		assertTrue(g.getVertexCount() == graph.vertices().size());
		assertTrue(g.getVertices().containsAll(graph.vertices()));
		assertTrue(g.getEdgeCount() == graph.edges().size());
		assertTrue(g.getEdges().containsAll(graph.edges()));
	}

}
