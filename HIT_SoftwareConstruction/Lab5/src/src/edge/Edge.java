package edge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vertex.Vertex;

/**
 * Represents general Edge in Graph. the Edges with same label are regarded
 * equal
 * <p>
 * it's Mutable
 * </p>
 */
public abstract class Edge implements Cloneable {
	/* information for edge */
	protected final String label;

	/**
	 * represent the weight of edge. if the edge is not weighted, it should be
	 * set to -1. else it should be set set to non-negative number
	 */
	private double weight;

	/* represent the vertices connected by the edge */
	Collection<Vertex> vertices;

	/**
	 * AF :
	 * <p>
	 * represents a general edge. the edge has a label information, weight and
	 * vertices it connect.
	 * </p>
	 * label:String; weight:double; vertices:HashSet
	 * 
	 * RI :
	 * <p>
	 * if the edge is weighted, the weight should be non-negative.
	 * </p>
	 */

	/**
	 * Edge constructor
	 * 
	 * @param label
	 *            label of vertex
	 * @param weight
	 *            weight of vertex, it should be set to -1. else it should be
	 *            set set to non-negative number
	 */
	public Edge(String label, double weight) {
		this.label = label;
		this.weight = weight;
		this.vertices = new ArrayList<>();
	}

	protected void checkRep() {
		// assert !label.isEmpty();
		// assert valid();
	}

	/**
	 * get label of edge
	 * 
	 * @return label of edge
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * get weight of edge
	 * 
	 * @return weight of edge
	 */
	public double getWeight() {
		return this.weight;
	}

	/**
	 * set weight
	 * 
	 * @param weight
	 *            weight to set
	 * @return return true if successful, return false if the weight < 0
	 */
	public boolean setWeight(double weight) {
		if (weight < 0)
			return false;
		this.weight = weight;
		checkRep();
		return true;
	}

	/**
	 * add vertices to edge. if the edge is HyperEdge, no limit to the
	 * vertices.size(). if the edge is directed edge, the vertices.size() == 2
	 * if the edge is loop, vertices.size() == 1.
	 * 
	 * @param vertices
	 *            vertices to add
	 * @return true if vertices added successfully
	 * @exception RuntimeException
	 *                throw if the edge is not valid after add operation
	 */
	abstract public boolean addVertices(List<Vertex> vertices);

	/**
	 * remove vertex from edge. this may make the edge an invalid edge.
	 * 
	 * @param vertex
	 *            vertex to remove
	 * @return true if the vertex exists and is removed successfully
	 */
	public boolean removeVertex(Vertex vertex) {
		boolean res = vertices.remove(vertex);
		return res;
	}

	/**
	 * check if the edge connect the vertex
	 * 
	 * @param vertex
	 *            vertex to check
	 * @return return true if vertex is connected.
	 */
	public boolean containVertex(Vertex vertex) {
		return vertices.contains(vertex);
	}

	/**
	 * get the vertices connected by edge
	 * 
	 * @return set of vertices connected by edge
	 */
	public Set<Vertex> vertices() {
		Set<Vertex> set = new HashSet<>();
		for (Vertex vertex : this.vertices) {
			set.add(vertex.clone());
		}
		return set;
	}

	/**
	 * get size of vertices connected by this edge
	 * 
	 * @return size of vertices connected by this edge
	 */
	public int vSize() {
		return vertices.size();
	}

	/**
	 * get the source vertices of the edge
	 * 
	 * @return return source vertices of the edge if the edge is valid. If the
	 *         edge is invalid, return an empty set.
	 */
	abstract public Set<Vertex> sourceVertices();

	/**
	 * get the target vertices of the edge
	 * 
	 * @return return target vertices of the edge if the edge is valid. If the
	 *         edg eis invalid, return an empty set.
	 */
	abstract public Set<Vertex> targetVertices();

	/**
	 * check if this edge is a valid edge
	 * 
	 * @return return true if the edge is valid.
	 */
	abstract public boolean valid();

	/**
	 * check if this edge is directed
	 * 
	 * @return return true if the edge is directed
	 */
	abstract public boolean isDirected();

	/**
	 * check if the edge can be loop. loop is a edge with same source and target
	 * vertex
	 * 
	 * @return return true if the edge can be loop.
	 */
	abstract public boolean loopable();

	/**
	 * check if the edge is hyper edge.
	 * 
	 * @return return true if the edge is hyper edge.
	 */
	public boolean isHyperEdge() {
		return false;
	}

	/**
	 * compare two edges by their labels
	 * 
	 * @return return true if the two edges have the same label
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Edge) {
			Edge edge = (Edge) obj;
			return edge.getLabel().equals(this.getLabel());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return label.hashCode();
	}

	/**
	 * Returns a string representation of the edge. In general, the toString
	 * method returns a string that "textually represents" this edge. The result
	 * should be a concise but informative representation that is easy for a
	 * person to read. It is recommended that all subclasses override this
	 * method.
	 * 
	 * @return a string representation of the edge.
	 */
	@Override
	public String toString() {
		return label;
	}

	/**
	 * clone the Edge. should be Override in concrete Edge class
	 * 
	 * @return return the clones Edge
	 * @exception RuntimeException
	 *                raise when the edge is invalid
	 */
	@Override
	abstract public Edge clone();

	public boolean isLoop() {
		if (isDirected())
			return sourceVertices().containsAll(targetVertices());
		else
			return false;
	}

	/**
	 * get an Edge Instance with only label. Only useful to search an Edge in
	 * Set
	 * 
	 * @return a common Edge with only label
	 */
	public static Edge common(String label) {
		return new Edge(label, -1) {

			@Override
			public boolean valid() {
				return true;
			}

			@Override
			public Set<Vertex> targetVertices() {
				return new HashSet<>();
			}

			@Override
			public Set<Vertex> sourceVertices() {
				return new HashSet<>();
			}

			@Override
			public boolean loopable() {
				return true;
			}

			@Override
			public boolean isDirected() {
				return true;
			}

			@Override
			public Edge clone() {
				return this;
			}

			@Override
			public boolean addVertices(List<Vertex> vertices) {
				return false;
			}
		};
	}
}
