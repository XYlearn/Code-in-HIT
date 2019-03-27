package util;

import edge.Edge;
import graph.Graph;
import vertex.Vertex;

/**
 * Context of application
 */
public class AppContext {
	Graph<Vertex, Edge> graph;

	/**
	 * Constructor
	 * 
	 * @param graph
	 *            context's graph
	 */
	public AppContext(Graph<Vertex, Edge> graph) {
		this.graph = graph;
	}

	/**
	 * get graph from context
	 * 
	 * @return graph of context
	 */
	public Graph<Vertex, Edge> getGraph() {
		return this.graph;
	}

	/**
	 * set the Context's graph
	 * 
	 * @param graph
	 *            graph to set
	 * @return return true
	 */
	public boolean setGraph(Graph<Vertex, Edge> graph) {
		this.graph = graph;
		return true;
	}
}
