/**
 * 
 */
package edge;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import vertex.Actor;
import vertex.Director;
import vertex.Movie;
import vertex.Vertex;

public class MovieDirectorRelationTest extends EdgeTest {

	/**
	 * Testing Strategies
	 * 
	 * for addVertices: covers add movie only, add actors only, and add actor and
	 * movie in correct order and add the second time.
	 * 
	 * for wrap: covers arguments valid and invalid(wrong type, wrong order)
	 */

	protected Movie movie;
	protected Director director1;
	protected Director director2;

	/**
	 * @see edge.EdgeTest#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		movie = Movie.wrap("movie", new String[] { "1988", "CN", "5.5" });
		director1 = Director.wrap("AD", new String[] { "M", "32" });
		director2 = Director.wrap("BC", new String[] { "F", "23" });
		v1 = movie;
		v2 = director2;
		v3 = director1;
		v4 = director2;
		e1024 = edgeInstance("12", Arrays.asList(v1, v2), 0.1);
		e1025 = edgeInstance("12", Arrays.asList(v1, v4), 0.1);
		e1 = edgeInstance("12", Arrays.asList(v1, v2), 0.1);
	}

	@Override
	protected MovieDirectorRelation edgeInstance(String label, List<Vertex> vertices, double weight) {
		return MovieDirectorRelation.wrap(label, vertices, weight);
	}
	
	@Override
	protected Actor vertexInstance(String label, String[] args) {
		if(args.length == 0)
			return Actor.wrap(label, args);
		return Actor.wrap(label, args);
	}

	/**
	 * Test method for {@link edge.MovieDirectorRelation#addVertices(java.util.List)}.
	 */
	@Test
	public void testAddVertices() {
		MovieDirectorRelation edge = new MovieDirectorRelation("ram", -1);
		assertFalse(edge.addVertices(Arrays.asList(movie)));
		assertFalse(edge.addVertices(Arrays.asList(director1, director1)));
		assertTrue(edge.addVertices(Arrays.asList(movie, director1)));
		assertFalse(edge.addVertices(Arrays.asList(director2)));
	}
	
	@Test
	public void testHashCode() {
		assertTrue(e1024.hashCode() == e1025.hashCode());
	}

	@Test
	public void testSetWeight() {
		assertFalse(e1024.setWeight(0.3));
		assertFalse(e1024.setWeight(-0.3));
	}

	@Test
	public void testRemoveVertex() {
		Edge edge = e1024;
		assertTrue(edge.removeVertex(v1));
		assertFalse(edge.containVertex(v1));
		assertFalse(edge.removeVertex(v1));
	}

	@Test
	public void testContainVertex() {
		Edge edge = e1024;
		assertTrue(edge.containVertex(v1));
		assertFalse(edge.containVertex(v3));
	}

	@Test
	public void testVertices() {
		Edge edge = e1024;
		assertTrue(edge.vertices().size() == 2);
		assertTrue(edge.vertices().contains(v1));
		assertTrue(edge.vertices().contains(v2));
		edge.vertices().remove(v1);
		assertTrue(edge.vertices().size() == 2);
	}

	@Test
	public void testVSize() {
		Edge edge = e1024;
		assertTrue(edge.vSize() == 2);
	}

	@Test
	public void testSourceVertices() {
	}

	@Test
	public void testTargetVertices() {
	}

	@Test
	public void testValid() {
		Edge edge = e1024;
		assertTrue(edge.valid());
		edge.removeVertex(v1);
		assertFalse(edge.valid());
		edge.removeVertex(v2);
		assertFalse(edge.valid());
	}
}
