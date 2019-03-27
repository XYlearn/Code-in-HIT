package factory.vertex;

import vertex.Vertex;

/**
 * Factory to generate Edge
 */
public abstract class VertexFactory {
	/**
	 * Constructor
	 */
	public VertexFactory() {
		super();
	}
	
	/**
	 * generate a Vertex 
	 * @param label vertex label
	 * @param args other information about the Vertexs
	 * @return generated Vertex instance of Vertex
	 */
	abstract public Vertex createVertex(String label, String[] args);
}
