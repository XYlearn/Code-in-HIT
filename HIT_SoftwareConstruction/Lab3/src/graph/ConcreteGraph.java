package graph;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edge.Edge;
import vertex.Vertex;

/**
 * Concrete graph class. it can be extended to different kind of graph
 *
 */
public class ConcreteGraph implements Graph<Vertex, Edge> {

	protected final Collection<Vertex> vertices;
	protected final Collection<Edge> edges;
	protected final String name;
	protected boolean directed;

	/**
	 * AF : use a HashSet of Vertex to represent vertices of graph; use a Hashset of
	 * Edge to represent edges of graph
	 * 
	 * RI : vertices is a set with no same vertices; edges is a set with no same
	 * vertices. Every vertex in edge is in vertices
	 * 
	 * safety from rep exposure: the Collection fields are protected and final, and
	 * can't be accessed from irrelevant classes, because of defensive copy.
	 */

	/**
	 * Constructor of ConcreteGraph
	 * 
	 * @param graph
	 *            name
	 */
	public ConcreteGraph(String name) {
		this.vertices = new HashSet<>();
		this.edges = new HashSet<>();
		this.name = name;
		this.directed = true;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isDirected() {
		return directed;
	}

	@Override
	public boolean addVertex(Vertex vertex) {
		return vertices.add(vertex.clone());
	}

	@Override
	public boolean removeVertex(Vertex vertex) {
		boolean removed = vertices.remove(vertex);
		// return false because vertex doesn't exist
		if (!removed)
			return false;

		for (Iterator<Edge> iterator = edges.iterator(); iterator.hasNext();) {
			Edge edge = iterator.next();
			if (edge.containVertex(vertex)) {
				// remove vertex
				edge.removeVertex(vertex);
				// if the vertex is invalid after operation, remove it.
				if (!edge.valid())
					iterator.remove();
			}
		}
		return true;
	}

	@Override
	public Set<Vertex> vertices() {
		Set<Vertex> res = new HashSet<>();
		for (Vertex vertex : vertices) {
			// defensive copy
			res.add(vertex.clone());
		}
		return res;
	}

	@Override
	public Map<Vertex, List<Double>> sources(Vertex target) {
		Map<Vertex, List<Double>> res = new HashMap<>();

		// if vertex is not in vertices, it has no sources
		if (!vertices.contains(target))
			return res;

		for (Edge edge : edges) {
			if (edge.targetVertices().contains(target)) {
				// get all source vertices
				Set<Vertex> sourceVertices = edge.sourceVertices();

				// add all source vertices to result
				for (Vertex source : sourceVertices) {
					if (res.containsKey(source)) {
						res.get(source).add(edge.getWeight());
					} else {
						res.put(source, Arrays.asList(edge.getWeight()));
					}
				}
			}
		}

		return res;
	}

	@Override
	public Map<Vertex, List<Double>> targets(Vertex source) {
		Map<Vertex, List<Double>> res = new HashMap<>();

		// if vertex is not in vertices, it has no sources
		if (!vertices.contains(source))
			return res;

		for (Edge edge : edges) {
			if (edge.sourceVertices().contains(source)) {
				// get all source vertices
				Set<Vertex> targetVertices = edge.targetVertices();

				// add all source vertices to result
				for (Vertex target : targetVertices) {
					if (res.containsKey(source)) {
						res.get(target).add(edge.getWeight());
					} else {
						res.put(target, Arrays.asList(edge.getWeight()));
					}
				}
			}
		}

		return res;
	}

	@Override
	public boolean addEdge(Edge edge) {
		// invalid edge
		if (!edge.valid())
			throw new IllegalArgumentException("Invalid edge argument");
		// if edge is in graph, it will not be contained again
		if (edges.contains(edge))
			return false;
		// if the edge has vertex not in vertices, it's invalid in the graph
		if (!vertices.containsAll(edge.vertices()))
			return false;

		return edges.add(edge.clone());
	}

	@Override
	public boolean removeEdge(Edge edge) {
		if (!edge.valid())
			throw new IllegalArgumentException("Invalid edge argument");
		return edges.remove(edge);
	}

	@Override
	public Set<Edge> edges() {
		Set<Edge> res = new HashSet<>();
		for (Edge edge : edges) {
			// defensive copy
			assert edge.valid();
			res.add(edge.clone());
		}

		return res;
	}

	@Override
	public int vertexCount() {
		return this.vertices.size();
	}

	@Override
	public int edgeCount() {
		return this.edges.size();
	}

	@Override
	public boolean containVertex(Vertex v) {
		return vertices.contains(v);
	}

	@Override
	public boolean containEdge(Edge e) {
		return edges.contains(e);
	}

}
