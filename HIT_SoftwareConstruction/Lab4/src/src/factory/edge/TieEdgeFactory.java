package factory.edge;

import java.util.List;

import edge.Tie;
import vertex.Vertex;

public abstract class TieEdgeFactory extends EdgeFactory {

	@Override
	abstract public Tie createEdge(String label, List<Vertex> vertices, double weight);

}
