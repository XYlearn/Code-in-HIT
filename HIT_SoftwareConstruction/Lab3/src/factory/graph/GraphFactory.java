package factory.graph;

import graph.Graph;

public abstract class GraphFactory<L, E> {
	/**
	 * Constructor
	 */
	public GraphFactory() {
		super();
	}

	/**
	 * create a graph from file. The error format line will be ignored
	 * 
	 * @param pathname
	 *            pathname of file to parse
	 * @return return an empty Graph
	 */
	abstract public Graph<L, E> createGraph(String graphname);

}
