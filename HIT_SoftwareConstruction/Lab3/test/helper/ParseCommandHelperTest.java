package helper;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edge.Edge;
import edge.FriendTie;
import edge.NetworkConnection;
import edge.SameMovieHyperEdge;
import edge.WordNeighborhood;
import graph.ConcreteGraph;
import graph.NetworkTopology;
import vertex.Actor;
import vertex.Computer;
import vertex.Person;
import vertex.Router;
import vertex.Server;
import vertex.Vertex;

public class ParseCommandHelperTest {

	/**
	 * Testing Strategies
	 * 
	 * for parseGraphType: test valid and invalid GraphType
	 * 
	 * for parseGraphName: test valid syntax and invalid syntax
	 * 
	 * for parseEdgeType: test valid and invalid EdgeType; test one type and
	 * multiple types
	 * 
	 * for parseEdgeBuild: test valid syntax and invalid syntax; test build one edge
	 * and multiple edges
	 * 
	 * for parseVertexType: test valid and invalid VertexType; test one command and
	 * multiple types
	 * 
	 * for parseVertexBuild: test valid syntax and invalid syntax; test build one
	 * vertex and multiple vertices
	 * 
	 * for parseHyperEdgeBuild: test valid syntax and invalid syntax;
	 * 
	 * for buildGraph: test the result.
	 * 
	 * for executeVertexAdd: test valid syntax and invalid syntax; test valid
	 * arguments and invalid arguments
	 * 
	 * for executeVertexDelete: test valid syntax and invalid syntax. test different
	 * kind of regex
	 * 
	 * for executeEdgeAdd: test valid syntax and invalid syntax. test valid
	 * arguments and invalid arguments;
	 * 
	 * for executeEdgeDelete: test valid syntax and invalid syntax. test different
	 * kinds of regex
	 * 
	 * for executeHyperEdgeAdd: test valid syntax and invalid syntax. test valid
	 * aruguments and invalid arguments
	 */

	protected ParseCommandHelper helper;
	protected String content;
	protected ConcreteGraph graph1;
	protected ConcreteGraph graph2;

	@Before
	public void setUp() throws Exception {
		helper = new ParseCommandHelper();
		File file = new File("test/helper/testcase.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] cbuf = new char[(int) file.length()];
		reader.read(cbuf);
		content = String.valueOf(cbuf);
		reader.close();
		// graph1 = helper.buildGraphFromFile("test/helper/testcase.txt");
		graph2 = ParseCommandHelper.buildGraphFromFile("test/helper/testcase2.txt");
	}

	/**
	 * Testing {@link ParseCommandHelper#parseGraphType(String)}. test valid and
	 * invalid GraphType
	 */
	@Test
	public void testParseGraphType() {
		try {
			Class<?> graphType = helper.parseGraphType(content);
			assertTrue(NetworkTopology.class.equals(graphType));
		} catch (BadGraphTypeException e) {
			assert false;
		}

		try {
			helper.parseGraphType("GraphType=NonGraph");
			assert false;
		} catch (BadGraphTypeException e) {
			assert true;
		}
	}

	/**
	 * Testing {@link ParseCommandHelper#parseGraphName(String)}. Test valid syntax
	 * and invalid syntax
	 */
	@Test
	public void testParseGraphName() {
		String name = helper.parseGraphName(content);
		assertEquals(name, "LabNetwork");
		name = helper.parseGraphName("asd");
		assertTrue(name.isEmpty());
	}

	/**
	 * Testing {@link ParseCommandHelper#parseEdgeType(String)}. test valid and
	 * invalid EdgeType; test one type and multiple types.
	 */
	@Test
	public void testParseEdgeType() {
		assertTrue(helper.parseEdgeType(content).containsAll(Arrays.asList(NetworkConnection.class)));
		content = "EdgeType = NetworkConnection, WordNeighborhood, FriendTie";
		assertTrue(helper.parseEdgeType(content)
				.containsAll(Arrays.asList(NetworkConnection.class, WordNeighborhood.class, FriendTie.class)));
		assertTrue(helper.parseEdgeType("").isEmpty());
	}

	/**
	 * Testing {@link ParseCommandHelper#parseEdgeBuild(String)}. test valid syntax
	 * and invalid syntax; test build one edge and multiple edges
	 */
	@Test
	public void testParseEdgeBuild() {
		helper.parseVertexType(content);
		helper.parseVertexBuild(content);
		assertTrue(helper.parseEdgeBuild("").isEmpty());
		List<Edge> res = helper.parseEdgeBuild(content);
		assertTrue(res.size() == 2);
		assertTrue(res.get(0).getLabel().equals("R1S1"));
		assertTrue(res.get(0) instanceof NetworkConnection);
		assertTrue(res.get(0).getWeight() == 100);
		assertTrue(res.get(0).vertices()
				.containsAll(Arrays.asList(Router.wrap("Router1", "127.0.0.1"), Server.wrap("Server1", "127.0.0.1"))));
	}

	/**
	 * Testing {@link ParseCommandHelper#parseVertexType(String)}.test valid and
	 * invalid VertexType; test one command and multiple types
	 */
	@Test
	public void testParseVertexType() {
		List<Class<?>> res = helper.parseVertexType("VertexType = Wrong");
		assertTrue(res.isEmpty());
		res = helper.parseVertexType("VertexType = Computer");
		assertTrue(res.contains(Computer.class) && res.size() == 1);
		res = helper.parseVertexType("VertexType = Computer, Router, Server");
		assertTrue(res.size() == 3);
		assertTrue(res.containsAll(Arrays.asList(Computer.class, Router.class, Server.class)));
	}

	/**
	 * Testing {@link ParseCommandHelper#parseVertexBuild(String)}.test valid syntax
	 * and invalid syntax; test build one vertex and multiple vertices
	 */
	@Test
	public void testParseVertexBuild() {
		assertTrue(helper.parseVertexBuild(content).isEmpty());
		helper.parseVertexType(content);

		List<Vertex> res = helper.parseVertexBuild("nothong");
		assertTrue(res.isEmpty());
		res = helper.parseVertexBuild("Vertex = <Computer1, Computer>");
		assertTrue(res.isEmpty());
		res = helper.parseVertexBuild(content);
		assertTrue(res.size() == 3);
		assertTrue(res.get(0).getLabel().equals("Computer1"));
		assertTrue(res.get(1).getLabel().equals("Server1"));
		assertTrue(res.get(2).getLabel().equals("Router1"));
	}

	/**
	 * Testing {@link ParseCommandHelper#parseHyperEdgeBuild(String)}. test valid
	 * syntax and invalid syntax;
	 */
	@Test
	public void testParseHyperEdgeBuild() {
		helper.parseVertexType("VertexType = Actor, Director");
		helper.parseVertexBuild("Vertex = <a1, Actor, <M, 32>>");
		helper.parseVertexBuild("Vertex = <a2, Actor, <F, 23>>");
		helper.parseVertexBuild("Vertex = <a3, Actor, <M, 55>>");
		assertTrue(helper.parseHyperEdgeBuild("").isEmpty());
		assertTrue(helper.parseHyperEdgeBuild("HyperEdge=<123, Wrong, <>>").isEmpty());
		List<Edge> res = helper.parseHyperEdgeBuild("HyperEdge = <hyper, SameMovieHyperEdge, {a1, a2, a3}>");
		assertTrue(res.size() == 1);
		Edge hyper = res.get(0);
		assertTrue(hyper.vertices().size() == 3);
	}

	/**
	 * Testing {@link ParseCommandHelper#buildGraph(String)}. test the result.
	 */
	@Test
	public void testBuildGraph() {
		ConcreteGraph graph = ParseCommandHelper.buildGraph(content);
		assertTrue(graph instanceof NetworkTopology);
		assertTrue(graph.getName().equals("LabNetwork"));
		assertTrue(graph.vertices().size() == 3);
		assertTrue(graph.edges().size() == 2);
	}

	/**
	 * Testing {@link ParseCommandHelper#executeVertexAdd(String)}. test valid
	 * syntax and invalid syntax; test valid arguments and invalid arguments
	 */
	@Test
	public void testExecuteVertexAdd() {
		ConcreteGraph graph = ParseCommandHelper.buildGraph("GraphName = test\nGraphType = ConcreteGraph");
		assertFalse(ParseCommandHelper.executeVertexAdd(graph, "vertex --add123 Word"));
		assertTrue(graph.vertexCount() == 0);

		assertTrue(ParseCommandHelper.executeVertexAdd(graph, "vertex --add person Person M 45"));
		assertTrue(graph.vertexCount() == 1);
		Vertex vertex = graph.vertices().iterator().next();
		assertTrue(vertex instanceof Person);
		assertTrue(((Person) vertex).isMale());
		assertTrue(((Person) vertex).getAge() == 45);

		graph = ParseCommandHelper.buildGraph("GraphName = test\nGraphType = ConcreteGraph");
		assertFalse(ParseCommandHelper.executeVertexAdd(graph, "vertex --add person Person Female 32"));
		assertTrue(graph.vertexCount() == 0);

		graph = ParseCommandHelper.buildGraph("GraphName = test\nGraphType = ConcreteGraph");
		assertTrue(ParseCommandHelper.executeVertexAdd(graph, "vertex  --add\tw1 Word"));
		assertTrue(graph.containVertex(Vertex.common("w1")));
		assertTrue(graph.vertexCount() == 1);
	}

	/**
	 * Testing {@link ParseCommandHelper#executeVertexDelete(String)}. test valid
	 * syntax and invalid syntax. test different kind of regex
	 */
	@Test
	public void testExecuteVertexDelete() throws IOException {
		ConcreteGraph graph = ParseCommandHelper.buildGraphFromFile("test/helper/testcase.txt");
		assertTrue(ParseCommandHelper.executeVertexDelete(graph, "vertex --delete \".*1\""));
		assertTrue(graph.vertexCount() == 0);

		graph = ParseCommandHelper.buildGraphFromFile("test/helper/testcase.txt");
		assertTrue(ParseCommandHelper.executeVertexDelete(graph, "vertex --delete \"Com\""));
		assertTrue(graph.containVertex(Vertex.common("Computer1")));

		graph = ParseCommandHelper.buildGraphFromFile("test/helper/testcase.txt");
		assertFalse(ParseCommandHelper.executeVertexDelete(graph, "vertex --delete"));
	}

	/**
	 * Testing {@link ParseCommandHelper#executeEdgeAdd(String)}. test valid syntax
	 * and invalid syntax. test valid arguments and invalid arguments;
	 */
	@Test
	public void testExecuteEdgeAdd() throws IOException {
		String content = "GraphType = NetworkTopology\r\n" + "GraphName = LabNetwork\r\n"
				+ "VertexType = Computer, Router, Server\r\n" + "Vertex = <Computer1, Computer, <192.168.1.101>>\r\n"
				+ "Vertex = <Server1, Server, <192.168.1.2>>\r\n" + "Vertex = <Router1, Router, <192.168.1.1>>";
		ConcreteGraph graph = ParseCommandHelper.buildGraph(content);
		assertFalse(ParseCommandHelper.executeEdgeAdd(graph, "edge --add edge NetworkConnection 100 Computer1, Computer2"));
		assertFalse(ParseCommandHelper.executeEdgeAdd(graph, "edge --add edge NetworkConnection 100 Computer1 Server1"));
		assertTrue(ParseCommandHelper.executeEdgeAdd(graph, "edge --add edge NetworkConnection 100 Computer1, Server1"));
		assertTrue(graph.edgeCount() == 1);
		Edge edge = graph.edges().iterator().next();
		assertTrue(edge.containVertex(Vertex.common("Computer1")));
		assertTrue(edge.containVertex(Vertex.common("Server1")));
	}

	/**
	 * Testing {@link ParseCommandHelper#executeEdgeDelete(String)}. test valid
	 * syntax and invalid syntax. test different kinds of regex
	 */
	@Test
	public void testExecuteEdgeDelete() throws IOException {
		ConcreteGraph graph = ParseCommandHelper.buildGraphFromFile("test/helper/testcase2.txt");
		assertFalse(ParseCommandHelper.executeEdgeDelete(graph, "edge --delete \"\""));
		assertFalse(ParseCommandHelper.executeEdgeDelete(graph, "edge --delete"));
		System.out.println(graph.edgeCount());
		assertTrue(graph.edgeCount() == 6);

		assertTrue(ParseCommandHelper.executeEdgeDelete(graph, "edge --delete \"GM.*\""));
		assertTrue(graph.edgeCount() == 4);

		assertTrue(ParseCommandHelper.executeEdgeDelete(graph, "edge --delete \"ActorsInSR\""));
		assertTrue(graph.edgeCount() == 3);
	}

	/**
	 * Testing {@link ParseCommandHelper#executeHyperEdgeAdd(String)}. test valid
	 * syntax and invalid syntax. test valid aruguments and invalid arguments
	 */
	@Test
	public void testExecuteHyperEdgeAdd() throws IOException {
		ConcreteGraph graph = ParseCommandHelper.buildGraphFromFile("test/helper/testcase2.txt");
		assertFalse(ParseCommandHelper.executeHyperEdgeAdd(graph, "hyperedge --add label SameMovieHyperEdge"));
		assertTrue(ParseCommandHelper.executeHyperEdgeAdd(graph, "hyperedge --add label SameMovieHyperEdge TimRobbins, TomHanks"));
		assertTrue(graph.edgeCount() == 7);
		assertTrue(graph.edges()
				.contains(SameMovieHyperEdge.wrap("label",
						Arrays.asList(Actor.wrap("TimRobbins", new String[] { "M", "32" }),
						Actor.wrap("TomHanks", new String[] { "M", "32" })), -1)));
	}
	
}
