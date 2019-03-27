package graph;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import edge.CommentTie;
import edge.Edge;
import edge.FriendTie;
import vertex.Person;
import vertex.Vertex;

public class SocialNetworkTest extends ConcreteGraphTest {

	/**
	 * Testing Strategies
	 * 
	 * for addEdge : covers add edge when graph has no edge and add edge when graph
	 * has edge
	 */

	ConcreteGraph graph;
	Edge edge1;
	Edge edge2;
	Edge edge3;
	Edge edge4;
	Vertex vertex1;
	Vertex vertex2;
	Vertex vertex3;
	
	@Override
	public SocialNetwork emptyInstance(String name) {
		return new SocialNetwork(name);
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		graph = emptyInstance("graph");
		vertex1 = Person.wrap("a", new String[] { "M", "13" });
		vertex2 = Person.wrap("b", new String[] { "F", "29" });
		vertex3 = Person.wrap("c", new String[] { "F", "18" });
		edge1 = FriendTie.wrap("e1", Arrays.asList(vertex1, vertex2), 0.5);
		edge2 = FriendTie.wrap("e2", Arrays.asList(vertex1, vertex3), 0.4);
		edge3 = FriendTie.wrap("e3", Arrays.asList(vertex2, vertex1), 0.6);
		edge4 = CommentTie.wrap("e4", Arrays.asList(vertex1, vertex2), 0.4);
	}

	/**
	 * Test {@link SocialNetwork#addEdge(Edge)}. covers add edge when graph has no
	 * edge and add edge when graph has edge
	 */
	@Test
	public void testAddEdge() {
		super.testAddEdge();
		graph.addVertex(vertex1);
		graph.addVertex(vertex2);
		graph.addVertex(vertex3);

		assertTrue(graph.addEdge(edge1));
		assertTrue(graph.edges().iterator().next().getWeight() == 1);
		assertTrue(graph.addEdge(edge2));
		for (Edge edge : graph.edges) {
			if (edge.equals(edge2))
				assertTrue(edge.getWeight() == 0.4);
			if (edge.equals(edge1))
				assertTrue(edge.getWeight() == 0.6);
		}
		assertTrue(graph.addEdge(edge3));
		for (Edge edge : graph.edges) {
			if (edge.equals(edge3))
				assertTrue(edge.getWeight() - 0.6 < 0.000000000001);
			if (edge.equals(edge2))
				assertTrue(edge.getWeight() - 0.16 < 0.000000000001);
			if (edge.equals(edge1))
				assertTrue(edge.getWeight() - 0.24 < 0.000000000001);
		}

		assertTrue(graph.addEdge(edge4));
	}

	@Test
	public void testRemoveEdge() {
		super.testRemoveEdge();
		graph.addVertex(vertex1);
		graph.addVertex(vertex2);
		graph.addVertex(vertex3);
		graph.addEdge(edge1);
		graph.addEdge(edge2);
		graph.addEdge(edge3);
		graph.addEdge(edge4);
		assertTrue(graph.removeEdge(edge4));
		for (Edge edge : graph.edges) {
			if (edge.equals(edge3))
				assertTrue(edge.getWeight() - 0.6 < 0.000000000001);
			if (edge.equals(edge2))
				assertTrue(edge.getWeight() - 0.16 < 0.000000000001);
			if (edge.equals(edge1))
				assertTrue(edge.getWeight() - 0.24 < 0.000000000001);
		}
		
		assertTrue(graph.removeEdge(edge2));
		for (Edge edge : graph.edges) {
			if (edge.equals(edge2))
				assertTrue(edge.getWeight() - 0.4 < 0.000000000001);
			if (edge.equals(edge1))
				assertTrue(edge.getWeight() - 0.6 < 0.000000000001);
		}
		
		assertTrue(graph.removeEdge(edge1));
		assertTrue(graph.edges().iterator().next().getWeight() == 1);
	}
	
	@Test
	@Override
	public void testTargets() {
		Vertex vertex1 = v1024;
		Vertex vertex2 = v1025;
		assertTrue(graph.sources(vertex1).isEmpty());

		graph.addVertex(vertex1);
		graph.addVertex(vertex2);
		graph.addEdge(e1026);
		assertTrue(graph.targets(vertex2).size() == 1);
		assertTrue(graph.targets(vertex2).keySet().iterator().next().equals(vertex1));

		graph.addEdge(e1024);
		assertTrue(graph.targets(vertex1).size() == 1);
		assertTrue(graph.targets(vertex1).keySet().iterator().next().equals(vertex2));
	}

	@Test
	@Override
	public void testSources() {
		Vertex vertex1 = v1024;
		Vertex vertex2 = v1025;
		assertTrue(graph.sources(vertex1).isEmpty());

		graph.addVertex(vertex1);
		graph.addVertex(vertex2);
		graph.addEdge(e1024);
		assertTrue(graph.sources(vertex2).size() == 1);
		assertTrue(graph.sources(vertex2).keySet().iterator().next().equals(vertex1));

		graph.addEdge(e1025);
		assertTrue(graph.sources(vertex1).size() == 1);
		assertTrue(graph.sources(vertex1).keySet().iterator().next().equals(vertex1));
	}
}
