package helper;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import edge.Edge;
import edge.NetworkConnection;
import edge.WordNeighborhood;
import graph.Graph;
import vertex.Router;
import vertex.Server;
import vertex.Vertex;
import vertex.Word;

public class GraphStoreHelperTest {

	protected Graph<Vertex, Edge> graph;
	protected GraphStoreHelper helper;

	@Before
	public void setUp() throws Exception {
		graph = ParseCommandHelper
				.buildGraphFromFile("test/src/helper/testcase.txt");
		helper = new GraphStoreHelper();
	}

	@Test
	public void testGraphNameExpression() {
		System.out.println(helper.graphNameExpression(graph));
		assertEquals("GraphName = LabNetwork",
				helper.graphNameExpression(graph));
	}

	@Test
	public void testGraphTypeExpression() {
		String expression = helper.graphTypeExpression(graph);
		System.out.print(expression);
		assertEquals("GraphType = NetworkTopology", expression);
	}

	@Test
	public void testVertexExpression() {
		String expression = helper.vertexExpression(Word.wrap("123"));
		System.out.println(expression);
		assertEquals("Vertex = <123, Word, <>>", expression);
		expression = helper.vertexExpression(Server.wrap("serv", "192.0.1.2"));
		System.out.println(expression);
		assertEquals("Vertex = <serv, Server, <192.0.1.2>>", expression);
	}

	@Test
	public void testEdgeExpression() {
		Word w1 = Word.wrap("w1");
		Word w2 = Word.wrap("w2");
		WordNeighborhood edge = WordNeighborhood.wrap("edge",
				java.util.Arrays.asList(w1, w2), 2);
		String expression = helper.edgeExpression(edge);
		System.out.println(expression);
		assertEquals("Edge = <edge, WordNeighborhood, 2.0, w1, w2, Yes>",
				expression);
	}

	@Test
	public void testVertexTypeExpression() {
		helper.vertexExpression(Word.wrap("123"));
		String expression = helper.vertexTypeExpression();
		System.out.println(expression);
		assertEquals("VertexType = Word", expression);
		helper.vertexExpression(Server.wrap("server", "127.0.0.1"));
		expression = helper.vertexTypeExpression();
		System.out.println(expression);
		assertEquals("VertexType = Word, Server", expression);
	}

	@Test
	public void testEdgeTypeExpression() {
		Word w1 = Word.wrap("w1");
		Word w2 = Word.wrap("w2");
		WordNeighborhood edge = WordNeighborhood.wrap("edge",
				java.util.Arrays.asList(w1, w2), 2);
		helper.edgeExpression(edge);
		String expression = helper.edgeTypeExpression();
		System.out.println(expression);
		assertEquals("EdgeType = WordNeighborhood", expression);
		Server s1 = Server.wrap("server", "127.0.0.1");
		Router r1 = Router.wrap("router", "127.0.0.2");
		NetworkConnection conn = NetworkConnection.wrap("conn",
				Arrays.asList(s1, r1), 100);
		helper.edgeExpression(conn);
		expression = helper.edgeTypeExpression();
		assertEquals("EdgeType = WordNeighborhood, NetworkConnection",
				expression);
	}

	@Test
	public void testConstruct() {
		helper.construct(graph);
		assertEquals("GraphType = NetworkTopology", helper.graphTypeExp);
		assertEquals("GraphName = LabNetwork", helper.graphNameExp);
		assertEquals("VertexType = Server, Computer, Router",
				helper.vertexTypeExp);
		assertEquals("EdgeType = NetworkConnection", helper.edgeTypeExp);
		assert helper.vertexExps
				.contains("Vertex = <Computer1, Computer, <192.168.1.101>>");
		assert helper.vertexExps
				.contains("Vertex = <Server1, Server, <192.168.1.2>>");
		assert helper.vertexExps
				.contains("Vertex = <Router1, Router, <192.168.1.1>>");
		for (String exp : helper.edgeExps)
			System.out.println(exp);
		assert helper.edgeExps.contains(
				"Edge = <R1S1, NetworkConnection, 100.0, Server1, Router1, Yes>");
		assert helper.edgeExps.contains(
				"Edge = <C1S1, NetworkConnection, 10.0, Server1, Computer1, Yes>");
	}

	@Test
	public void testStore() throws IOException {
		File tmp = new File("rsrc/tmp.txt");
		if (tmp.exists())
			tmp.delete();
		tmp.createNewFile();
		GraphStoreHelper.store(graph, tmp);
	}

}
