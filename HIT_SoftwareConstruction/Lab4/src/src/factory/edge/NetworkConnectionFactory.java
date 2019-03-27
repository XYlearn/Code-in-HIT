package factory.edge;

import java.util.List;

import edge.NetworkConnection;
import vertex.Vertex;

public class NetworkConnectionFactory extends EdgeFactory {

	@Override
	public NetworkConnection createEdge(String label, List<Vertex> vertices, double weight) {
		return NetworkConnection.wrap(label, vertices, weight);
	}

}
