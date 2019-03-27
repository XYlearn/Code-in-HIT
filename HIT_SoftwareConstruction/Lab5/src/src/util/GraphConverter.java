package util;

import edge.Edge;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import vertex.Vertex;

public class GraphConverter {
	/**
	 * convert graph.Graph to edu.uci.ics.jung.graph.Graph
	 * 
	 * @param src
	 *            graph.Graph to convert
	 * @return edu.uci.ics.jung.graph.Graph instance
	 */
	public static Graph<Vertex, Edge> convert2Jung(graph.Graph<Vertex, Edge> src) {
		Graph<Vertex, Edge> dst = null;
		if (src.isDirected()) {
			dst = new DirectedSparseGraph<>();
		} else {
			dst = new UndirectedSparseGraph<>();
		}
		for (Vertex vertex : src.vertices())
			dst.addVertex(vertex);

		if (src.isDirected()) {
			for (Edge edge : src.edges()) {
				dst.addEdge(edge, edge.sourceVertices().iterator().next(), edge.targetVertices().iterator().next());
			}
		} else {
			for (Edge edge : src.edges()) {
				dst.addEdge(edge, edge.vertices());
			}
		}

		return dst;
	}

}
