package factory.graph;

import graph.MovieGraph;

/**
 * Factory for MovieGraph
 */
public class MovieGraphFactory extends ConcreteGraphFactory {

	@Override
	public MovieGraph createGraph(String name) {
		return new MovieGraph(name);
	}
}
