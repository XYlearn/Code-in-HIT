	package graph;

/**
 * Represents a MovieGraph whose vertex types are {@link Movie},
 * {@link Director} and {@link Actor} and edge types are
 * {@link MovieActorRelation}, {@link MovieDirectorRelation} and
 * {@link SameMovieHyperEdge}. And MovieActorRelation connect Movie and its
 * actor, MovieDirectorRelation connect Movie and its director and
 * SameMovieHyperEdge connects Actors play in the same Movie
 */
public class MovieGraph extends ConcreteGraph {
	/**
	 * AF: Represents a MovieGraph whose vertex types are {@link Movie},
	 * {@link Director} and {@link Actor} and edge types are
	 * {@link MovieActorRelation}, {@link MovieDirectorRelation} and
	 * {@link SameMovieHyperEdge}
	 * 
	 * RI: MovieActorRelation connect Movie and its actor, MovieDirectorRelation
	 * connect Movie and its director and SameMovieHyperEdge connects Actors play in
	 * the same Movie
	 * 
	 * safety from rep exposure: see {@link ConcreteGraph}
	 */

	/**
	 * Constructor
	 * 
	 * @param name
	 *            graph name
	 */
	public MovieGraph(String name) {
		super(name);
		this.directed = false;
	}
}
