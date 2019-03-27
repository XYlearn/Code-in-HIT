package factory.graph;

import edge.Edge;
import graph.ConcreteGraph;
import vertex.Vertex;

/**
 * Factory for ConcreteGraph
 */
public abstract class ConcreteGraphFactory extends GraphFactory<Vertex, Edge> {
	
	@Override
	public ConcreteGraph createGraph(String name) {
		return new ConcreteGraph(name);
	}

}
