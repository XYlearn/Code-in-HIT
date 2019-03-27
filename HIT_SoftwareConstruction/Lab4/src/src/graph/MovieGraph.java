package graph;

import java.util.Arrays;
import java.util.List;

import edge.Edge;
import edge.MovieActorRelation;
import edge.MovieDirectorRelation;
import edge.SameMovieHyperEdge;
import vertex.Actor;
import vertex.Director;
import vertex.Movie;
import vertex.Vertex;

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

	public static List<Class<? extends Vertex>> vertexWhiteList = Arrays.asList(Director.class, Actor.class, Movie.class);
	public static List<Class<? extends Edge>> edgeWhiteList = Arrays.asList(MovieActorRelation.class,
			MovieDirectorRelation.class, SameMovieHyperEdge.class);

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
	
	@Override
	protected void checkRep() {
		super.checkRep();
	}
	
	@Override
	public boolean allowHyperEdge() {
		return true;
	}
}
