package factory.edge;

import java.util.List;

import edge.SameMovieHyperEdge;
import vertex.Vertex;

public class SameMovieHyperEdgeFactory extends EdgeFactory {

	@Override
	public SameMovieHyperEdge createEdge(String label, List<Vertex> vertices, double weight) {
		return SameMovieHyperEdge.wrap(label, vertices, weight);
	}

}
