package edge;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import vertex.Vertex;

/**
 * Represents general Tie in SocialNetwork. It extends Edge. It is an weighted
 * directed edge with no loop that means edge target shouldn't be equal to edge
 * source. And the weight should in range (0, 1]. Tie will be not used directly,
 * instead its subclasses such as {@link FriendTie}, {@link ForwardTie},
 * {@link CommentTie} will be used
 */
public class Tie extends Edge {

	/**
	 * AF : see Parent Class. vertices.get(0) is source vertex, vertices.get(1)
	 * is target vertex
	 * 
	 * RI : vertices is and ArrayList, vertices.size() == 2 and vertices.get(0)
	 * not equal to vertices.get(1)
	 * 
	 * safety from rep exposure : information can't be modified after added
	 */

	/**
	 * Constructor of Tie. package visible.
	 * 
	 * @param label
	 *            name of Friend Tie
	 * @param weight
	 *            weight of edge, in range(0, 1]
	 */
	protected Tie(String label, double weight) {
		super(label, weight);
	}

	/**
	 * set weight
	 * 
	 * @param weight
	 *            weight to set
	 * @return return true if successful, return false if the weight <= 0
	 */
	@Override
	public boolean setWeight(double weight) {
		if (weight <= 0 || weight > 1)
			return false;
		return super.setWeight(weight);
	}

	/**
	 * Add vertices to Tie, the first element of vertices is source vertex of
	 * edge, and the second element of vertices is target vertex of edge. if the
	 * source and target has been set or the vertices doesn't correspond to
	 * representations, it does nothing and will return false
	 * 
	 * @param vertices
	 *            list of vertices to add, the format is mentioned above
	 * @return return true if the vertices were added
	 */
	@Override
	public boolean addVertices(List<Vertex> vertices) {
		if (this.vertices.size() > 0)
			return false;
		if (vertices.size() != 2)
			return false;
		return this.vertices.addAll(vertices);
	}

	@Override
	public Set<Vertex> sourceVertices() {
		if (!this.valid())
			throw new RuntimeException("Invalid Edge");
		Set<Vertex> vertices = new HashSet<>();
		vertices.add(this.vertices.iterator().next().clone());
		return vertices;
	}

	@Override
	public Set<Vertex> targetVertices() {
		if (!this.valid())
			throw new RuntimeException("Invalid Edge");
		Set<Vertex> vertices = new HashSet<>();
		Iterator<Vertex> iterator = this.vertices.iterator();
		iterator.next();
		vertices.add(iterator.next().clone());
		return vertices;
	}

	@Override
	public boolean valid() {
		return vertices.size() == 2 && new HashSet<>(vertices).size() == 2
				&& getWeight() > 0 && getWeight() <= 1;
	}

	@Override
	public String toString() {
		return this.label;
	}

	@Override
	public Tie clone() {
		// return wrap(this.getClass(), getLabel(),
		// Arrays.asList(sourceVertices().iterator().next(),
		// targetVertices().iterator().next()),
		// this.getWeight());
		return this;
	}

	/**
	 * generate Tie Instance
	 * 
	 * @param type
	 *            Concrete type of Tie
	 * @param label
	 *            label of the Tie
	 * @param vertices
	 *            vertices of the edge, the first is source and the second is
	 *            target. and the to elements should be different
	 * @param weight
	 *            weight of the Tie
	 * @return return new instance if generate success; if the arguments doesn't
	 *         match {@link Tie} description or the type is not Found, function
	 *         will fail and return null.
	 */
	protected static Tie wrap(Class<? extends Tie> type, String label,
			List<Vertex> vertices, double weight) {
		if (vertices.size() != 2 && new HashSet<>(vertices).size() != 2)
			return null;
		if (vertices.contains(null))
			return null;
		try {
			// construct instance of type
			Tie tie = type.getDeclaredConstructor(String.class, double.class)
					.newInstance(label, weight);
			if (tie.addVertices(vertices) && tie.valid())
				return tie;
			else
				return null;
		} catch (Exception e) {
			// Exception caught, return null
			return null;
		}
	}

	@Override
	public boolean loopable() {
		return false;
	}

	@Override
	public boolean isDirected() {
		return true;
	}

}
