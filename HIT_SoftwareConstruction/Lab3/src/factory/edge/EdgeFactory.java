package factory.edge;

import java.util.List;

import edge.Edge;
import vertex.Vertex;

/**
 * Factory to generate Edge
 */
public abstract class EdgeFactory {
	/**
	 * Constructor
	 */
	public EdgeFactory() {
		super();
	}

	/**
	 * create a edge
	 * 
	 * @param label
	 *            edge's label
	 * @param vertices
	 *            vertices the edge contains
	 * @param weight
	 *            edge's weight.
	 * @return generated edge
	 */
	abstract public Edge createEdge(String label, List<Vertex> vertices, double weight);
}
