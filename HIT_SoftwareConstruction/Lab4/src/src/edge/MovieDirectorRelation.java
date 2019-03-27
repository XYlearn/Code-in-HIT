package edge;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import vertex.Director;
import vertex.Movie;
import vertex.Vertex;

/**
 * Represents the relation between Movie and Director. It is an undirected and
 * non-weighted edge.
 */
public class MovieDirectorRelation extends Edge {

	/**
	 * AF : Actor and Movie are saved as vertices in Edge
	 * 
	 * RI : vertices.size() == 2 and the first vertex in list is Instance of Movie;
	 * The second vertex in list is Instance of Director. the weight of edge should
	 * be -1.
	 * 
	 * safety from rep exposure: see Edeg
	 */

	/**
	 * Constructor
	 * 
	 * @param label
	 *            label of edge
	 * @param weight
	 *            whenever what it is, the edge's weight would be -1
	 */
	protected MovieDirectorRelation(String label, double weight) {
		super(label, -1);
	}

	/**
	 * Add vertices to edge, the first element of vertices is instance of
	 * {@link Movie}, and the second element of vertices is instance of
	 * {@link Director}. if the source and target has been set or the vertices
	 * doesn't correspond to representations, it will do nothing and return false.
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
		Iterator<Vertex> iterator = vertices.iterator();
		Vertex v1 = iterator.next();
		Vertex v2 = iterator.next();
		if (!((v1 instanceof Movie && v2 instanceof Director) || (v1 instanceof Director && v2 instanceof Movie)))
			return false;
		return this.vertices.addAll(vertices);
	}

	/**
	 * do nothing
	 * 
	 * @return return false
	 */
	@Override
	@Deprecated
	public boolean setWeight(double weight) {
		return false;
	}

	@Override
	@Deprecated
	public Set<Vertex> sourceVertices() {
		if (!valid())
			return new HashSet<>();
		return vertices();
	}

	@Override
	@Deprecated
	public Set<Vertex> targetVertices() {
		if (!valid())
			return new HashSet<>();
		return vertices();
	}

	/**
	 * check if the edge is a valid MovieDirectorRelation, see
	 * {@link MovieDirectorRelation}
	 * 
	 * @return return true if the MovieDirectorRelation is valid
	 */
	@Override
	public boolean valid() {
		if (vertices.size() != 2)
			return false;
		Iterator<Vertex> iterator = vertices.iterator();
		Vertex source = iterator.next();
		Vertex target = iterator.next();
		if (!(source instanceof Movie) || !(target instanceof Director))
			return false;
		return true;
	}

	@Override
	public Edge clone() {
		Iterator<Vertex> iterator = vertices.iterator();
		Vertex movie = iterator.next();
		Vertex actor = iterator.next();
		return wrap(this.getLabel(), Arrays.asList(movie, actor), -1);
	}

	/**
	 * Create MovieDirectorRelation Instance
	 * 
	 * @param label
	 *            edge's label
	 * @param vertices
	 *            vertices of the edge, the first is source and the second is target
	 * @param weight
	 *            weight of the edge, don't have actual meaning
	 * @return return a MovieDirectorRelationship Instance connecting movie and
	 *         actor. if the vertices are invalid, return null
	 */
	public static MovieDirectorRelation wrap(String label, List<Vertex> vertices, double weight) {
		if (vertices.size() != 2)
			return null;
		if (vertices.contains(null))
			return null;
		MovieDirectorRelation edge = new MovieDirectorRelation(label, -1);
		if (edge.addVertices(Arrays.asList(vertices.get(0).clone(), vertices.get(1).clone())) && edge.valid())
			return edge;
		else
			return null;
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
