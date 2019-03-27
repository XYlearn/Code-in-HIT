package factory.edge;

import java.util.List;

import edge.CommentTie;
import vertex.Vertex;

public class CommentTieEdgeFactory extends TieEdgeFactory {

	@Override
	public CommentTie createEdge(String label, List<Vertex> vertices, double weight) {
		return CommentTie.wrap(label, vertices, weight);
	}

}
