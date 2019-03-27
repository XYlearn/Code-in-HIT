package graph;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edge.Edge;
import vertex.Vertex;

public class DirectedGraph extends ConcreteGraph {

	private ConcreteGraph graph;
	
	public DirectedGraph(String name) {
		super(name);
		graph = new ConcreteGraph(name);
		this.directed = true;
	}
	
	@Override
	public String getName() {
		return graph.getName();
	}

	@Override
	public boolean isDirected() {
		return true;
	}

	@Override
	public boolean addVertex(Vertex vertex) {
		return graph.addVertex(vertex.clone());
	}

	@Override
	public boolean removeVertex(Vertex vertex) {
		return graph.removeVertex(vertex);
	}

	@Override
	public Set<Vertex> vertices() {
		return graph.vertices();
	}

	@Override
	public Map<Vertex, List<Double>> sources(Vertex target) {
		return graph.sources(target);
	}

	@Override
	public Map<Vertex, List<Double>> targets(Vertex source) {
		return graph.targets(source);
	}

	@Override
	public boolean addEdge(Edge edge) {
		if(edge.getWeight() < 0)
			return false;
		else
			return graph.addEdge(edge);
	}

	@Override
	public boolean removeEdge(Edge edge) {
		return graph.removeEdge(edge);
	}

	@Override
	public Set<Edge> edges() {
		return graph.edges();
	}

	@Override
	public int vertexCount() {
		return graph.vertexCount();
	}

	@Override
	public int edgeCount() {
		return graph.edgeCount();
	}

	@Override
	public boolean containVertex(Vertex v) {
		return graph.containVertex(v);
	}

	@Override
	public boolean containEdge(Edge e) {
		return graph.containEdge(e);
	}
}
