package factory.graph;

import graph.GraphPoet;

/**
 * Facotry for GraphPoet
 */
public class GraphPoetFactory extends ConcreteGraphFactory {

	@Override
	public GraphPoet createGraph(String name) {
		return new GraphPoet(name);
	}
}
