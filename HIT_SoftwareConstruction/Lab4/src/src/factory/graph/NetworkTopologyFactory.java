package factory.graph;

import graph.NetworkTopology;

/**
 * Factory for NetworkTopology
 */
public class NetworkTopologyFactory extends ConcreteGraphFactory {

	@Override
	public NetworkTopology createGraph(String name) {
		return new NetworkTopology(name);
	}
}
