package edge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vertex.Actor;
import vertex.Vertex;

/**
 * Represents a hyperEdge connecting Actors in the same Movie. All vertices in
 * the Edge should be instance of Actor. And the edge at least has one vertex
 *
 */
public class SameMovieHyperEdge extends Edge {

	/**
	 * AF: The Actors are stored in vertices
	 * 
	 * RI: weight is always -1; not directed and can't be loop
	 * 
	 * safety from rep exposure: all field are not public and use defensive copy
	 */

	/**
	 * Constructor
	 * 
	 * @param label
	 *            edge label
	 * @param weight
	 *            useless because it's non-weighted
	 */
	protected SameMovieHyperEdge(String label, double weight) {
		super(label, -1);
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

	/**
	 * Add vertices to hyper edge.
	 * 
	 * @param vertices
	 *            list of vertices to add, all vertices must be instance of Actor.
	 * 
	 * @return return true if vertices are added successfully
	 */
	@Deprecated
	@Override
	public boolean addVertices(List<Vertex> vertices) {
		vertices = new ArrayList<>(vertices);
		// the first time to add vertices

		List<Vertex> actors = new ArrayList<>();

		for (Vertex vertex : vertices) {
			if (!(vertex instanceof Actor))
				return false;
			actors.add(vertex.clone());
		}
		return this.vertices.addAll(new HashSet<Vertex>(actors));
	}

	/**
	 * return set of all vertices. same as {@link SameMovieHyperEdge#vertices()}
	 */
	@Override
	public Set<Vertex> sourceVertices() {
		return vertices();
	}

	/**
	 * return set of all vertices. same as {@link SameMovieHyperEdge#vertices()}
	 */
	@Override
	public Set<Vertex> targetVertices() {
		return vertices();
	}

	/**
	 * hyper edge is valid if the size >= 2
	 * 
	 * @return return true if the actor size >= 2
	 */
	@Override
	public boolean valid() {
		for(Vertex vertex : vertices)
			if(!(vertex instanceof Actor))
				return false;
		return vertices.size() >= 2;
	}

	@Override
	public SameMovieHyperEdge clone() {
		return wrap(this.getLabel(), (List<Vertex>) vertices, -1);
	}

	/**
	 * Generate SameMovieHyperEdge Instance
	 * 
	 * @param label
	 *            edge's label
	 * @param vertices
	 *            vertices to add, the vertices.size() >= 2 and the elements should
	 *            be Instance of Actor.
	 * @param weight
	 *            weight of edge, will not be used
	 * @return return generated Instance of SameMovieHyperEdge if the parameters are
	 *         valid, else return null instead
	 */
	public static SameMovieHyperEdge wrap(String label, List<Vertex> vertices, double weight) {
		// invalid size
		if (vertices.size() < 2)
			return null;
		if(vertices.contains(null))
			return null;

		// check all left vertices
		List<Vertex> actors = new ArrayList<>(vertices.size());
		for (Vertex vertex : vertices) {
			if (vertex instanceof Actor)
				actors.add(vertex);
			else
				return null;
		}

		SameMovieHyperEdge edge = new SameMovieHyperEdge(label, -1);
		edge.addVertices(vertices);
		if (edge.valid())
			return edge;
		else
			return null;
	}

	@Override
	public boolean loopable() {
		return true;
	}

	@Override
	public boolean isHyperEdge() {
		return true;
	}

	@Override
	public boolean isDirected() {
		return false;
	}

}
