package factory.edge;

import java.util.List;

import edge.FriendTie;
import vertex.Vertex;

public class FriendTieEdgeFactory extends TieEdgeFactory {

	@Override
	public FriendTie createEdge(String label, List<Vertex> vertices, double weight) {
		return FriendTie.wrap(label, vertices, weight);
	}

}
