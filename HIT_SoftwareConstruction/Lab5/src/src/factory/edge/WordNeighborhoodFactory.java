package factory.edge;

import java.util.List;

import edge.WordNeighborhood;
import vertex.Vertex;

public class WordNeighborhoodFactory extends EdgeFactory {

	@Override
	public WordNeighborhood createEdge(String label, List<Vertex> vertices, double weight) {
		return WordNeighborhood.wrap(label, vertices, weight);
	}

}
