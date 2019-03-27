package edge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import vertex.Vertex;
import vertex.Word;

/**
 * edge for PoetGraph
 *
 */
public class WordNeighborhood extends Edge {

	/**
	 * AF : vertices is ArrayList, use vertices.get(0) to represent source vertex;
	 * use vertices.get(1) to represent target vertex. the label is in format :
	 * source->target.
	 * 
	 * RI : edge is valid only when vertices.size == 2; weight > 0
	 * 
	 * safety from rep exposure : label private and final, the weight is settable
	 * but will always fit RI. all sourceVertices and targetVertices methods will
	 * only return the clones set.
	 */

	/**
	 * WordNeighborhood Constructor
	 * 
	 * @param label
	 *            label of WordNeighborhood
	 * @param weight
	 *            weight of edge
	 */
	private WordNeighborhood(String label, double weight) {
		super(label, weight);
		vertices = new ArrayList<>();
	}

	/**
	 * add vertices to edge. shouldn't be used ouside the class
	 * 
	 * @param vertices
	 *            vertices to add, vertices.size() should be 2 to be added
	 *            successfully. the vertices can only be added once
	 * @return true if vertices added successfully
	 * @exception RuntimeException
	 *                throw if the edge is not valid after add operation
	 */
	@Override
	@Deprecated
	public boolean addVertices(List<Vertex> vertices) {
		if (this.vertices.size() > 0)
			return false;
		if (vertices.size() == 2) {
			return this.vertices.addAll(vertices);
		} else if (vertices.size() == 1) {
			return this.vertices.addAll(vertices) && this.vertices.addAll(vertices);
		} else {
			return false;
		}
	}

	@Override
	public Set<Vertex> sourceVertices() {
		Set<Vertex> res = new HashSet<>();
		if (!valid())
			return res;
		res.add(vertices.iterator().next().clone());
		return res;
	}

	@Override
	public Set<Vertex> targetVertices() {
		Set<Vertex> res = new HashSet<>();
		if (!valid())
			return res;
		Iterator<Vertex> ite = vertices.iterator();
		ite.next();
		res.add(ite.next().clone());
		return res;
	}

	@Override
	public boolean valid() {
		return vertices.size() == 2;
	}

	@Override
	public String toString() {
		return getLabel();
	}

	@Override
	public Edge clone() {
		Edge edge = new WordNeighborhood(getLabel(), getWeight());
		List<Vertex> verticies = new ArrayList<>();
		verticies.addAll(this.vertices);
		edge.addVertices(verticies);
		return edge;
	}

	/**
	 * wrap an wordNeighborhood
	 * 
	 * @param source
	 *            source of the edge
	 * @param target
	 *            target of the edge
	 * @param weight
	 *            weight of the edge
	 * @return the wrapped WordNeighborhood edge. if argument is invalid, return
	 *         null
	 */
	public static WordNeighborhood wrap(String source, String target, double weight) {
		return wrap(source + "->" + target, Arrays.asList(Word.wrap(source), Word.wrap(target)), weight);
	}

	/**
	 * wrap an wordNeighborhood
	 * 
	 * @param source
	 *            source of the edge
	 * @param target
	 *            target of the edge
	 * @param weight
	 *            weight of the edge
	 * @return the wrapped WordNeighborhood edge. if argument is invalid, return
	 *         null
	 */
	public static WordNeighborhood wrap(String label, List<Vertex> vertices, double weight) {
		WordNeighborhood edge = new WordNeighborhood(label, weight);
		if (new HashSet<>(vertices).size() == 1)
			edge.addVertices(Arrays.asList(vertices.get(0).clone()));
		else
			edge.addVertices(Arrays.asList(vertices.get(0).clone(), vertices.get(1).clone()));
		if (!edge.valid())
			return null;
		return edge;
	}

}
