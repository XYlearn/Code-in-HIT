package helper.centrality;

import edge.Edge;
import graph.Graph;
import vertex.Vertex;

public interface CentralityStrategy {

	/**
	 * calculate centrality of Vertex v in Graph g
	 * 
	 * @param g
	 *            Graph
	 * @param v
	 *            Vertex
	 * @return centrality of Vertex v in Graph g
	 */
	double calculate(Graph<Vertex, Edge> g, Vertex v);

}
