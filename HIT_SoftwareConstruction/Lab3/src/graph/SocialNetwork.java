package graph;

import edge.Edge;
import helper.GraphMetrics;
import vertex.Vertex;

/**
 * A directed, weighted, multiGraph represent SocialNetwork. it has three kinds
 * of edge: {@link edge.Tie}, {@link edge.CommentTie} and
 * {@link edge.ForwardTie} and its Vertex type is {@link vertex.Person}
 */
public class SocialNetwork extends ConcreteGraph {
	/**
	 * AF : almost same to ConcreteGraph
	 * 
	 * RI : no loop edge; sum of weight equals to 1 if edges is not empty.
	 * 
	 * safety from rep exposure: same to ConcreteGraph
	 */

	/**
	 * Constructor
	 * 
	 * @param name
	 *            graph name
	 */
	public SocialNetwork(String name) {
		super(name);
		this.directed = true;
	}

	/**
	 * Add an edge to graph. if the edge has been in graph or it has vertex which
	 * doesn't exists in graph, it will not be added and return false. the edge's
	 * weight will be set to 1 if it's the only edge in graph, otherwise after the
	 * edge is added, all edge weight will be updated to wb * (1 - wa) where wb is
	 * the original weight of edges in the graph, wa is the added edge's weight.
	 * 
	 * @param edge
	 *            Edge to add
	 * @return if the edge does not exists in graph and is added successfully,
	 *         return true.
	 */
	@Override
	public boolean addEdge(Edge edge) {
		edge = edge.clone();
		// edge already exists in graph
		if (edges.contains(edge))
			return false;
		// invalid weight
		if (!edge.valid())
			throw new IllegalArgumentException("Invalid edge!");

		// if the edge has vertex not in vertices, it's invalid in the graph
		if (!vertices.containsAll(edge.vertices()))
			return false;

		// the edge will be the only edge in the graph
		if (edges.isEmpty()) {
			edge.setWeight(1);
		}

		// update all edges' weight in the graph
		double balance = 1 - edge.getWeight();
		for (Edge e : edges) {
			e.setWeight(e.getWeight() * balance);
		}

		return edges.add(edge);
	}

	/**
	 * Remove an edge from graph. and adjust the weight of each edge in graph
	 * 
	 * @param edge
	 *            Edge to remove
	 * @return if the edge exists in graph and is removed successfully, return true
	 */
	@Override
	public boolean removeEdge(Edge edge) {
		if (!edges.contains(edge))
			return false;

		if (!edge.valid())
			throw new IllegalArgumentException("Invalid edge!");

		// update edges weight in the graph
		double balance = 1 - edge.getWeight();
		for (Edge e : edges) {
			e.setWeight(e.getWeight() / balance);
		}
		return edges.remove(edge);
	}

	/**
	 * Get weight of vertex. Weight of vertex represent the influence of a Person in
	 * the SocialNetwork
	 * 
	 * @param vertex
	 *            Vertex in Graph
	 * @return return weight of graph if it exists in Graph, else return a negative
	 *         number
	 */
	public double vertexWeight(Vertex vertex) {
		return GraphMetrics.inDegreeCentrality(this, vertex);
	}
}
