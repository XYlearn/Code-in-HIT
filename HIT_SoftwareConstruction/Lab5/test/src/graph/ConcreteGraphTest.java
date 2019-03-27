/**
 * 
 */
package graph;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edge.Edge;
import edge.WordNeighborhood;
import vertex.Vertex;
import vertex.Word;

/**
 * @author XHWhy
 *
 */
public class ConcreteGraphTest {

	/**
	 * Testing Strategies
	 * 
	 * for ConcreteGraph: check whether edges and vertices are empty
	 * 
	 * for addVertex: add vertex exists and not exists
	 * 
	 * for removeVertex: remove vertex exists and not exists
	 * 
	 * for vertices: call when graph is empty and not empty
	 * 
	 * for sources: call when vertex has no source and when has sources. covers loop
	 * edge source and directed edge source. and test for not-existed vertex.
	 * 
	 * for targets: call when vertex has no source and when has targets. covers loop
	 * edge target and directed edge target. and test for not-existed vertex.
	 * 
	 * for addEdge: covers adding edge exists and not exists. and add edge with
	 * vertex not exists in graph
	 * 
	 * for removeEdge: covers removing edge which exists and not exists
	 * 
	 * for edges: covers call when graph has no edge and graph has edge(s)
	 * 
	 */

	protected ConcreteGraph graph;
	
	protected Vertex v1;
	protected Vertex v2;
	protected Vertex v3;
	protected Vertex v1024;
	protected Vertex v1025;
	protected Vertex v1026;
	
	protected Edge e1;
	protected Edge e2;
	protected Edge e1024;
	protected Edge e1025;
	protected Edge e1026;
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		graph = emptyInstance("graph");
		
		v1024 = Word.wrap("v1");
		v1025 = Word.wrap("v2");
		v1026 = Word.wrap("v1");
		
		e1024 = WordNeighborhood.wrap("e1", Arrays.asList(v1024, v1025), 0.3);
		e1025 = WordNeighborhood.wrap("e2", Arrays.asList(v1024, v1024), 0.2);
		e1026 = WordNeighborhood.wrap("e3", Arrays.asList(v1025, v1024), 0.3);
	}

	protected ConcreteGraph emptyInstance(String name) {
		return new ConcreteGraph(name);
	}

	protected Vertex vertexInstance(String label, String[] args) {
		return Word.wrap(label);
	}
	
	protected Edge edgeInstance(String label, List<Vertex> vertices, double weight) {
		return WordNeighborhood.wrap(label, vertices, weight);
	}

	/**
	 * Test method for {@link graph.ConcreteGraph#ConcreteGraph()}.
	 */
	@Test
	public void testConcreteGraph() {
		assertTrue(graph.vertices.isEmpty());
		assertTrue(graph.edges.isEmpty());
	}

	/**
	 * Test method for {@link graph.ConcreteGraph#addVertex(vertex.Vertex)}. covers
	 * test for add vertex exists and not exists.
	 */
	@Test
	public void testAddVertex() {
		Vertex vertex = v1024;
		Vertex vertex1 = v1024;
		assertTrue(graph.addVertex(vertex));
		assertFalse(graph.addVertex(vertex1));
		assertTrue(graph.vertices.size() == 1);
	}

	/**
	 * Test method for {@link graph.ConcreteGraph#removeVertex(vertex.Vertex)}.
	 * covers remove vertex exists and not exists.
	 */
	@Test
	public void testRemoveVertex() {
		Vertex vertex1 = v1025;
		Vertex vertex2 = v1026;
		graph.addVertex(vertex1);
		assertTrue(graph.removeVertex(vertex1));
		assertFalse(graph.removeVertex(vertex2));
	}

	/**
	 * Test method for {@link graph.ConcreteGraph#removeVertex(vertex.Vertex)}.
	 * covers remove vertex with edge.
	 */
	@Test
	public void testRemoveVertexWithEdge() {
		Vertex vertex1 = v1024;
		Vertex vertex2 = v1025;
		graph.addVertex(vertex1);
		graph.addVertex(vertex2);
		graph.addEdge(e1024);
		assertTrue(graph.removeVertex(vertex1));
		assertTrue(graph.edges.isEmpty());
	}

	/**
	 * Test method for {@link graph.ConcreteGraph#vertices()}. covers call when
	 * graph is empty and not empty
	 */
	@Test
	public void testVertices() {
		assertTrue(graph.vertices.isEmpty());
		Vertex vertex = v1024;
		graph.addVertex(vertex);
		assertTrue(graph.vertices.size() == 1);
	}

	/**
	 * Test method for {@link graph.ConcreteGraph#sources(vertex.Vertex)}. call when
	 * graph has no source and has sources. covers loop source and directed edge
	 * source. and test for not-existed vertex.
	 */
	@Test
	public void testSources() {
		Vertex vertex1 = v1024;
		Vertex vertex2 = v1025;
		assertTrue(graph.sources(vertex1).isEmpty());

		graph.addVertex(vertex1);
		graph.addVertex(vertex2);
		graph.addEdge(e1024);
		assertTrue(graph.sources(vertex2).size() == 1);
		assertTrue(graph.sources(vertex2).keySet().iterator().next().equals(vertex1));
		assertTrue(graph.sources(vertex2).values().iterator().next().get(0) == 0.3);

		graph.addEdge(e1025);
		assertTrue(graph.sources(vertex1).size() == 1);
		assertTrue(graph.sources(vertex1).keySet().iterator().next().equals(vertex1));
		assertTrue(graph.sources(vertex1).values().iterator().next().get(0) == 0.2);
	}

	/**
	 * Test method for {@link graph.ConcreteGraph#targets(vertex.Vertex)}. call when
	 * vertex has no source and when has targets. covers loop edge target and
	 * directed edge target. and test for not-existed vertex.
	 */
	@Test
	public void testTargets() {
		Vertex vertex1 = v1024;
		Vertex vertex2 = v1025;
		assertTrue(graph.sources(vertex1).isEmpty());

		graph.addVertex(vertex1);
		graph.addVertex(vertex2);
		graph.addEdge(e1026);
		assertTrue(graph.targets(vertex2).size() == 1);
		assertTrue(graph.targets(vertex2).keySet().iterator().next().equals(vertex1));
		assertTrue(graph.targets(vertex2).values().iterator().next().get(0) == 0.3);

		graph.addEdge(e1024);
		assertTrue(graph.targets(vertex1).size() == 1);
		assertTrue(graph.targets(vertex1).keySet().iterator().next().equals(vertex2));
		assertTrue(graph.targets(vertex1).values().iterator().next().get(0) == 0.3);
	}

	/**
	 * Test method for {@link graph.ConcreteGraph#addEdge(edge.Edge)}. covers adding
	 * edge exists and not exists. and add edge with vertex not exists in graph
	 */
	@Test
	public void testAddEdge() {
		Vertex vertex1 = v1024;
		Vertex vertex2 = v1025;
		graph.addVertex(vertex1);
		assertFalse(graph.addEdge(e1026));
		graph.addVertex(vertex2);
		assertTrue(graph.addEdge(e1026));
		assertTrue(graph.addEdge(e1024));
	}

	/**
	 * Test method for {@link graph.ConcreteGraph#removeEdge(edge.Edge)}. covers
	 * removing edge which exists and not exists
	 */
	@Test
	public void testRemoveEdge() {
		Vertex vertex1 = v1024;
		Vertex vertex2 = v1025;
		graph.addVertex(vertex1);
		graph.addVertex(vertex2);
		graph.addEdge(e1026);
		assertTrue(graph.removeEdge(e1026));
		assertFalse(graph.removeEdge(e1026));
	}

	/**
	 * Test method for {@link graph.ConcreteGraph#edges()}. covers call when graph
	 * has no edge and graph has edge(s)
	 */
	@Test
	public void testEdges() {
		assertTrue(graph.edges().isEmpty());
		Vertex vertex1 = v1024;
		Vertex vertex2 = v1025;
		graph.addVertex(vertex1);
		graph.addVertex(vertex2);
		graph.addEdge(e1026);
		assertTrue(graph.edges().size() == 1);
		assertTrue(graph.edges.iterator().next().equals(e1026));
	}

}
