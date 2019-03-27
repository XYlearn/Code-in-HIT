package factory.edge;

import java.util.List;

import edge.ForwardTie;
import vertex.Vertex;

public class ForwardTieEdgeFactory extends TieEdgeFactory {

	@Override
	public ForwardTie createEdge(String label, List<Vertex> vertices, double weight) {
		return ForwardTie.wrap(label, vertices, weight);
	}

}
