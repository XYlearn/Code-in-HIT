package vertex;

/**
 * Represent a general Vertex in Graph. The Vertex with the same label is
 * regarded equal
 * <p>
 * It's Mutable Generally
 * </p>
 */
public abstract class Vertex implements Cloneable {
	protected final String label; // label information for vertex

	/**
	 * AF : Represent a General Vertex with label and other information
	 * 
	 * RI : label is non-empty string
	 * 
	 * safety from rep exposure : label field is private and final
	 */

	/**
	 * constructor
	 * 
	 * @param label
	 *            vertex's label
	 */
	public Vertex(String label) {
		this.label = label;
	}
	
	protected void checkRep() {
		assert !label.isEmpty();
	}

	/**
	 * add detail information for specific vertex
	 * 
	 * @param args
	 *            information to add
	 * @exception IllegalArgumentException
	 *                throws when the args is invalid
	 */
	abstract public void fillVertexInfo(String[] args) throws IllegalArgumentException;

	/**
	 * get method for label
	 * 
	 * @return vertex's label
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * compare two vertices by their labels
	 * 
	 * @return return true if the two vertices have the same label
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vertex) {
			Vertex vertex = (Vertex) obj;
			// compare by label field
			return vertex.getLabel().equals(this.getLabel());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return label.hashCode();
	}

	/**
	 * Returns a string representation of the vertex. In general, the toString
	 * method returns a string that "textually represents" this vertex. The result
	 * should be a concise but informative representation that is easy for a person
	 * to read. It is recommended that all subclasses override this method.
	 * 
	 * @return a string representation of the vertex.
	 */
	@Override
	public String toString() {
		return label;
	}

	/**
	 * clone the Vertex. should be Override in concrete Vertex class
	 * 
	 * @return return the clones Vertex
	 */
	@Override
	abstract public Vertex clone();
	
	/**
	 * generate a common Vertex
	 * @param label label of vertex
	 * @return generated common Vertex
	 */
	public static Vertex common(String label) {
		return new CommonVertex(label);
	}
}

/**
 * common vertex that has only label field
 */
class CommonVertex extends Vertex {
	CommonVertex(String label) {
		super(label);
	}

	@Override
	public void fillVertexInfo(String[] args) throws IllegalArgumentException {}

	@Override
	public Vertex clone() {
		return this;
	}
}
