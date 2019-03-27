package factory.graph;

import graph.SocialNetwork;

/**
 * Factory for SocialNetwork
 */
public class SocialNetworkFactory extends ConcreteGraphFactory {

	@Override
	public SocialNetwork createGraph(String name) {
		return new SocialNetwork(name);
	}
}
