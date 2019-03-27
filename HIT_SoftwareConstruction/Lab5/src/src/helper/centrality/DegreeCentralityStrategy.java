package helper.centrality;

import edge.Edge;
import graph.Graph;
import helper.GraphMetrics;
import vertex.Vertex;

public class DegreeCentralityStrategy implements CentralityStrategy {

	@Override
	public double calculate(Graph<Vertex, Edge> g, Vertex v) {
		return GraphMetrics.degreeCentrality(g, v);
	}

}
