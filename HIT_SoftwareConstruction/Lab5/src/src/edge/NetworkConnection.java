package edge;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import vertex.Computer;
import vertex.Server;
import vertex.Vertex;

/**
 * Represents Network Connection in NetworkTopology. The edge is directed and
 * weighted. Edge's weight is non-negative and the two points of edge can't be
 * the same or are both Instance of Server or are both Instance of Computer.
 */
public class NetworkConnection extends Edge {

	/**
	 * AF : represents a general edge. the edge has a label information, weight
	 * and vertices it connect.label:String; ip:String; weight:double;
	 * vertices:HashSet
	 * 
	 * RI : Is directed and weight is non-negative and the two points of edge
	 * can't be the same or are both Instance of Server or are both Instance of
	 * Computer.
	 * 
	 * safety from rep exposure: defensive copy and can't add additional
	 * vertices after wrap
	 */

	/**
	 * Constructor
	 * 
	 * @param label
	 *            edge label
	 * @param weight
	 *            weight should > 0
	 */
	protected NetworkConnection(String label, double weight) {
		super(label, weight);
	}

	/**
	 * add vertices to edge. Can't add vertices if the edge already has vertex
	 * 
	 * @param vertices
	 *            vertices to add, vertices.size() should be 2 to be added
	 *            successfully. the vertices can only be added once
	 * @return true if vertices added successfully, return false if the edge is
	 *         not valid after add operation
	 */
	@Override
	@Deprecated
	public boolean addVertices(List<Vertex> vertices) {
		if (this.vertices.size() > 0)
			return false;
		if (vertices.size() == 2) {
			Iterator<Vertex> iterator = vertices.iterator();
			Vertex source = iterator.next();
			Vertex target = iterator.next();
			if (!connectable(source, target))
				return false;
			return this.vertices.addAll(vertices);
		} else {
			return false;
		}
	}

	@Override
	public Set<Vertex> sourceVertices() {
		Set<Vertex> res = new HashSet<>();
		if (!valid())
			return res;
		res.add(vertices.iterator().next());
		return res;
	}

	@Override
	public Set<Vertex> targetVertices() {
		Set<Vertex> res = new HashSet<>();
		if (!valid())
			return res;
		Iterator<Vertex> ite = vertices.iterator();
		ite.next();
		res.add(ite.next());
		return res;
	}

	@Override
	public boolean valid() {
		// check vertices size and weight range
		if (vertices.size() != 2 || getWeight() < 0)
			return false;
		// check whether it's a loop
		Iterator<Vertex> iterator = vertices.iterator();
		Vertex source = iterator.next();
		Vertex target = iterator.next();
		if (!connectable(source, target))
			return false;
		else
			return true;
	}

	@Override
	public Edge clone() {
		// if (valid())
		// return wrap(label, (List<Vertex>) vertices, getWeight());
		// else
		// throw new RuntimeException("Invalid Edge");
		return this;
	}

	/**
	 * Generate NetworkConnection Instance
	 * 
	 * @param label
	 *            edge's label
	 * @param vertices
	 *            vertices of the edge, size must be 2 and fit the
	 *            representation in class document
	 * @param weight
	 *            weight of the edge, must be positive
	 * @return return new Instance of edge if the arguments are valid. else
	 *         return null
	 */
	public static NetworkConnection wrap(String label, List<Vertex> vertices,
			double weight) {
		if (vertices.size() != 2)
			return null;
		if (vertices.contains(null))
			return null;
		NetworkConnection edge = new NetworkConnection(label, weight);
		if (edge.addVertices(Arrays.asList(vertices.get(0).clone(),
				vertices.get(1).clone()))) {
			if (edge.valid())
				return edge;
			else
				return null;
		} else
			return null;
	}

	/**
	 * check if two vertices are connectable. Two vertices are connectable only
	 * if they are instance of {@link Computer}, {@link Router} or
	 * {@link Server} and can't be equal to each other .In addition,
	 * {@link Computer} can't connect to {@link Computer} directly, same to
	 * {@link Server}.
	 * 
	 * @param source
	 *            source Vertex
	 * @param target
	 *            target Vertex
	 * @return return true if the two vertices match the description above
	 */
	protected static boolean connectable(Vertex source, Vertex target) {
		return !(source.equals(target)
				|| (source instanceof Computer && target instanceof Computer)
				|| (source instanceof Server && target instanceof Server));
	}

	@Override
	public boolean loopable() {
		return false;
	}

	@Override
	public boolean isDirected() {
		return false;
	}

}
