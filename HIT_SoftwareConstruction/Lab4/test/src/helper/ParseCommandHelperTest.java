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
import exception.CommandException;
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
		File file = new File("test/src/helper/testcase.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] cbuf = new char[(int) file.length()];
		reader.read(cbuf);
		content = String.valueOf(cbuf);
		reader.close();
		// graph1 = helper.buildGraphFromFile("test/helper/testcase.txt");
		graph2 = ParseCommandHelper.buildGraphFromFile("test/src/helper/testcase2.txt");
	}

	/**
	 * Testing {@link ParseCommandHelper#parseGraphType(String)}. test valid and
	 * invalid GraphType
	 */
	@Test
	public void testParseGraphType() {
		try {
			Class<?> graphType = helper.parseGraphType("GraphType = NetworkTopology");
			assertTrue(NetworkTopology.class.equals(graphType));
		} catch (CommandException e) {
			assert false;
		}

		try {
			helper.parseGraphType("GraphType=NonGraph");
			assert false;
		} catch (CommandException e) {
			assert true;
		}
	}

	/**
	 * Testing {@link ParseCommandHelper#parseGraphName(String)}. Test valid syntax
	 * and invalid syntax
	 */
	@Test
	public void testParseGraphName() {
		String name = helper.parseGraphName("GraphName = LabNetwork");
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
		try {
			helper.parseGraphType("GraphType = ConcreteGraph");
			assertTrue(helper.parseEdgeType("EdgeType = NetworkConnection")
					.containsAll(Arrays.asList(NetworkConnection.class)));

			content = "EdgeType = NetworkConnection, WordNeighborhood, FriendTie";
			assertTrue(helper.parseEdgeType(content)
					.containsAll(Arrays.asList(NetworkConnection.class, WordNeighborhood.class, FriendTie.class)));
			assertTrue(helper.parseEdgeType("").isEmpty());
		} catch (CommandException e) {
			assert false;
		}
	}

	/**
	 * Testing {@link ParseCommandHelper#parseEdgeBuild(String)}. test valid syntax
	 * and invalid syntax; test build one edge and multiple edges
	 */
	@Test
	public void testParseEdgeBuild() {
		try {
			helper.parseGraphType("GraphType = NetworkTopology");
			helper.parseVertexType("VertexType = Computer, Router, Server");
			helper.parseVertexBuild("Vertex = <Computer1, Computer, <192.168.1.101>>");
			helper.parseVertexBuild("Vertex = <Server1, Server, <192.168.1.2>>");
			helper.parseVertexBuild("Vertex = <Router1, Router, <192.168.1.1>>");
			helper.parseEdgeType("EdgeType = NetworkConnection");
			assertTrue(helper.parseEdgeBuild("") == null);
			Edge res = helper.parseEdgeBuild("Edge = <R1S1, NetworkConnection, 100, Router1, Server1, No>");
			assertTrue(null != res);
			assertTrue(res.getLabel().equals("R1S1"));
			assertTrue(res instanceof NetworkConnection);
			assertTrue(res.getWeight() == 100);
			assertTrue(res.vertices().containsAll(
					Arrays.asList(Router.wrap("Router1", "127.0.0.1"), Server.wrap("Server1", "127.0.0.1"))));
		} catch (CommandException e) {
			assert false;
		}
		try {
			String cont = "GraphType = NetworkTopology\n" + "VertexType = Computer, Router, Server\n"
					+ "Vertex = <Computer1, Computer, <192.168.1.101>>\n"
					+ "Vertex = <Server1, Server, <192.168.1.2>>\n" + "Vertex = <Router1, Router, <192.168.1.1>>\n"
					+ "EdgeType = NetworkConnection\n"
					+ "Edge = <R1C1, NetworkConnection, 100, Router1, Computer1, N>\n";
		
			ParseCommandHelper.buildGraph(cont);
			assert false;
		} catch (CommandException e) {
			assertTrue(!e.isIgnorable());
			assertTrue(e.getLineNo() == 6);
		}

		try {
			String cont = "GraphType = NetworkTopology\n" + "VertexType = Computer, Router, Server\n"
					+ "Vertex = <Computer1, Computer, <192.168.1.101>>\n"
					+ "Vertex = <Server1, Server, <192.168.1.2>>\n" + "Vertex = <Router1, Router, <192.168.1.1>>\n"
					+ "EdgeType = NetworkConnection\n"
					+ "Edge = <R1C1, NetworkConnection, 100, Router1, Computer1, No>\n"
					+ "Edge = <R1C1Dup, NetworkConnection, 100, Router1, Computer1, No>\n";
			ParseCommandHelper.buildGraph(cont);
		} catch (CommandException e) {
			assert false;
		}
	}

	/**
	 * Testing {@link ParseCommandHelper#parseVertexType(String)}.test valid and
	 * invalid VertexType; test one command and multiple types
	 */
	@Test
	public void testParseVertexType() {
		try {
			List<Class<?>> res = helper.parseVertexType("VertexType = Wrong");
			assertTrue(res.isEmpty());
			assert false;
		} catch (CommandException e) {
			assert true;
		}
		try {
			List<Class<?>> res = helper.parseVertexType("VertexType = Computer");
			assertTrue(res.contains(Computer.class) && res.size() == 1);
			res = helper.parseVertexType("VertexType = Computer, Router, Server");
			assertTrue(res.size() == 3);
			assertTrue(res.containsAll(Arrays.asList(Computer.class, Router.class, Server.class)));
		} catch (CommandException e) {
			assert false;
		}
	}

	/**
	 * Testing {@link ParseCommandHelper#parseVertexBuild(String)}.test valid syntax
	 * and invalid syntax; test build one vertex and multiple vertices
	 */
	@Test
	public void testParseVertexBuild() {
		try {
			assertTrue(null == helper.parseVertexBuild(content));
			helper.parseVertexType(content);
			assert true;
		} catch (CommandException e) {
			assert false;
		}
		try {
			helper.parseGraphType("GraphType = NetworkTopology");
			helper.parseVertexType("VertexType = Computer, Server");
			Vertex res = helper.parseVertexBuild("nothing");
			assertTrue(null == res);
			res = helper.parseVertexBuild("Vertex = <Computer1, Computer>");
			assertTrue(res == null);
			res = helper.parseVertexBuild("Vertex = <Computer1, Computer, <192.168.1.101>>");
			assertTrue(res != null);
			assertTrue(res.getLabel().equals("Computer1"));
		} catch (CommandException e) {
			assert false;
		}

		try {
			assertTrue(null == helper.parseVertexBuild("Vertex = <Computer2, Computer, <192.168.1.1>"));
		} catch (CommandException e) {
			assert false;
		}

		try {
			helper.parseVertexBuild("Vertex = <Server2, Server, <>>");
			assert false;
		} catch (CommandException e) {
			assertTrue(!e.isIgnorable());
		}

		try {
			helper.parseVertexBuild("Vertex = <Router1, Router, <192.168.3.2>>");
			assert false;
		} catch (CommandException e) {
			assertTrue(!e.isIgnorable());
		}

		try {
			String cont = "GraphType=NetworkTopology\n" + "GraphName=test\n"
					+ "VertexType = Computer, Server, Router, Word\n" + "Vertex = <W1, Word, <>>\n";
			ParseCommandHelper.buildGraph(cont);
			assert false;
		} catch (CommandException e) {
			assertTrue(!e.isIgnorable());
			assertTrue(e.getLineNo() == 3);
		}
	}

	/**
	 * Testing {@link ParseCommandHelper#parseHyperEdgeBuild(String)}. test valid
	 * syntax and invalid syntax;
	 */
	@Test
	public void testParseHyperEdgeBuild() {
		try {
			helper.parseGraphType("GraphType = MovieGraph");
			helper.parseVertexType("VertexType = Actor, Director");
			helper.parseVertexBuild("Vertex = <a1, Actor, <M, 32>>");
			helper.parseVertexBuild("Vertex = <a2, Actor, <F, 23>>");
			helper.parseVertexBuild("Vertex = <a3, Actor, <M, 55>>");
			assertTrue(helper.parseHyperEdgeBuild("") == null);
			assertTrue(helper.parseHyperEdgeBuild("HyperEdge=<123, Wrong, <>>") == null);
			Edge res = helper.parseHyperEdgeBuild("HyperEdge = <hyper, SameMovieHyperEdge, {a1, a2, a3}>");
			assertTrue(res != null);
			assertTrue(res.vertices().size() == 3);
		} catch (CommandException e) {
			assert false;
		}
	}

	/**
	 * Testing {@link ParseCommandHelper#buildGraph(String)}. test the result.
	 */
	@Test
	public void testBuildGraph() throws CommandException {
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
	public void testExecuteVertexAdd() throws CommandException {
		ConcreteGraph graph = ParseCommandHelper.buildGraph("GraphName = test\nGraphType = ConcreteGraph");
		try {
			assertFalse(ParseCommandHelper.executeVertexAdd(graph, "vertex --add123 Word"));
			assertTrue(graph.vertexCount() == 0);
			assert false;
		} catch (CommandException e) {
			// System.err.println(e.getMessage());
			assert true;
		}

		assertTrue(ParseCommandHelper.executeVertexAdd(graph, "vertex --add person Person M 45"));
		assertTrue(graph.vertexCount() == 1);
		Vertex vertex = graph.vertices().iterator().next();
		assertTrue(vertex instanceof Person);
		assertTrue(((Person) vertex).isMale());
		assertTrue(((Person) vertex).getAge() == 45);

		graph = ParseCommandHelper.buildGraph("GraphName = test\nGraphType = ConcreteGraph");
		try {
			ParseCommandHelper.executeVertexAdd(graph, "vertex --add person Person Female 32");
			assert false;
		} catch (CommandException e) {
			assert true;
		}
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
	public void testExecuteVertexDelete() throws IOException, CommandException {
		ConcreteGraph graph = ParseCommandHelper.buildGraphFromFile("test/src/helper/testcase.txt");
		assertTrue(ParseCommandHelper.executeVertexDelete(graph, "vertex --delete \".*1\""));
		assertTrue(graph.vertexCount() == 0);

		graph = ParseCommandHelper.buildGraphFromFile("test/src/helper/testcase.txt");
		assertTrue(ParseCommandHelper.executeVertexDelete(graph, "vertex --delete \"Com\""));
		assertTrue(graph.containVertex(Vertex.common("Computer1")));

		graph = ParseCommandHelper.buildGraphFromFile("test/src/helper/testcase.txt");
		try {
			ParseCommandHelper.executeVertexDelete(graph, "vertex --delete");
			assert false;
		} catch (CommandException e) {
			assert true;
		}
	}

	/**
	 * Testing {@link ParseCommandHelper#executeEdgeAdd(String)}. test valid syntax
	 * and invalid syntax. test valid arguments and invalid arguments;
	 */
	@Test
	public void testExecuteEdgeAdd() throws IOException, CommandException {
		String content = "GraphType = NetworkTopology\r\n" + "GraphName = LabNetwork\r\n"
				+ "VertexType = Computer, Router, Server\r\n" + "Vertex = <Computer1, Computer, <192.168.1.101>>\r\n"
				+ "Vertex = <Server1, Server, <192.168.1.2>>\r\n" + "Vertex = <Router1, Router, <192.168.1.1>>";
		ConcreteGraph graph = ParseCommandHelper.buildGraph(content);
		try {
			ParseCommandHelper.executeEdgeAdd(graph, "edge --add edge NetworkConnection 100 Computer1, Computer2");
			assert false;
		} catch (CommandException e) {
			assert true;
		}
		try {
			ParseCommandHelper.executeEdgeAdd(graph, "edge --add edge NetworkConnection 100 Computer1 Server1");
			assert false;
		} catch (CommandException e) {
			assert true;
		}
		assertTrue(
				ParseCommandHelper.executeEdgeAdd(graph, "edge --add edge NetworkConnection 100 Computer1, Server1"));
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
	public void testExecuteEdgeDelete() throws IOException, CommandException {
		ConcreteGraph graph = ParseCommandHelper.buildGraphFromFile("test/src/helper/testcase2.txt");
		try {
			assertFalse(ParseCommandHelper.executeEdgeDelete(graph, "edge --delete \"\""));
			assert false;
		} catch (CommandException e) {
			assert true;
		}
		try {
			assertFalse(ParseCommandHelper.executeEdgeDelete(graph, "edge --delete"));
			assert false;
		} catch (CommandException e) {
			assert true;
		}
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
	public void testExecuteHyperEdgeAdd() throws IOException, CommandException {
		ConcreteGraph graph = ParseCommandHelper.buildGraphFromFile("test/src/helper/testcase2.txt");
		try {
			ParseCommandHelper.executeHyperEdgeAdd(graph, "hyperedge --add label SameMovieHyperEdge");
			assert false;
		} catch (CommandException e) {
			assert true;
		}
		assertTrue(ParseCommandHelper.executeHyperEdgeAdd(graph,
				"hyperedge --add label SameMovieHyperEdge TimRobbins, TomHanks"));
		assertTrue(graph.edgeCount() == 7);
		assertTrue(graph.edges().contains(
				SameMovieHyperEdge.wrap("label", Arrays.asList(Actor.wrap("TimRobbins", new String[] { "M", "32" }),
						Actor.wrap("TomHanks", new String[] { "M", "32" })), -1)));
	}

}
