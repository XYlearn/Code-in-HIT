package graph;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Graph interface
 *
 * @param <L>
 *            Vertex Class of Graph
 * @param <E>
 *            Edge Class of Graph
 */
public interface Graph<L, E> {

	/**
	 * Generate an empty Graph
	 * 
	 * @return
	 */
	public static <L, E> Graph<L, E> empty() {
		throw new RuntimeException("unimplemented");
	}
	
	/**
	 * get Name of Graph
	 * @return name of Graph
	 */
	public String getName();

	/**
	 * Add a vertex to graph
	 * 
	 * @param vertex
	 *            vertex to add
	 * @return return true if the vertex not exist, and add successfully
	 */
	public boolean addVertex(L vertex);

	/**
	 * Remove a vertex from graph. if the vertex belongs to a both-ends edge, the
	 * edge will be deleted; if the vertex belongs to a Hyperedge, the Hyperedge
	 * will be deleted if the Hyperedge doesn't fit Hyperedge definition any more
	 * after the vertex was removed from it.
	 * 
	 * @param vertex
	 *            vertex to remove
	 * @return return true if the vertex exists in the graph and is removed
	 *         successfully
	 */
	public boolean removeVertex(L vertex);

	/**
	 * Get set of vertices in the graph
	 * 
	 * @return set of vertices in the graph
	 */
	public Set<L> vertices();

	/**
	 * Get a {@link Map} where keys represents sources vertices which has a edge to
	 * target and the value represent the weight of the edge
	 * 
	 * @param target
	 *            the vertex which we are going to find source vertices connect to
	 * 
	 * @return return a map which is defined above
	 */
	public Map<L, List<Double>> sources(L target);

	/**
	 * Get a {@link Map} where keys represents target vertices which has a edge to
	 * source and the value represent the weight of the edge
	 * 
	 * @param source
	 *            the vertex which we are going to find target vertices connect to
	 * @return return a map which is defined above
	 */
	public Map<L, List<Double>> targets(L source);

	/**
	 * Add an edge(including hyper-edge) to graph. if the edge has been in graph or
	 * it has vertex which doesn't exists in graph, it will not be added and return
	 * false
	 * 
	 * @param edge
	 *            Edge to add
	 * @return if the edge does not exists in graph and is added successfully,
	 *         return true.
	 */
	public boolean addEdge(E edge);

	/**
	 * Remove an edge(including hyper-edge) to graph. if the edge doesn't exists in
	 * graph, it will not be added and return false
	 * 
	 * @param edge
	 *            Edge to remove
	 * @return if the edge exists in graph and is removed successfully, return true
	 */
	public boolean removeEdge(E edge);

	/**
	 * Get set of edges(including hyper-edge) in the graph
	 * 
	 * @return Set of edges in the graph
	 */
	public Set<E> edges();

	/**
	 * check if the Graph is directed
	 * 
	 * @return return true if the graph is directed
	 */
	public boolean isDirected();

	/**
	 * get the number of vertices in graph
	 * 
	 * @return the number of vertices in graph
	 */
	public int vertexCount();

	/**
	 * get the number of edges in graph
	 * 
	 * @return the number of edges in graph
	 */
	public int edgeCount();

	/**
	 * check if VertexType v in graph
	 * 
	 * @param v
	 *            vertex
	 * @return return true if v is in graph
	 */
	public boolean containVertex(L v);

	/**
	 * check if EdgeType e in graph
	 * 
	 * @param e
	 *            edge
	 * 
	 * @return return true if e is in graph
	 */
	public boolean containEdge(E e);
}
