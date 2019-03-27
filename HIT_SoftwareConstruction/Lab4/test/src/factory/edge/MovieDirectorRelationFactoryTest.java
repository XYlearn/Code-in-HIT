/**
 * 
 */
package factory.edge;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import edge.MovieDirectorRelation;
import vertex.Director;
import vertex.Movie;

/**
 * @author XHWhy
 *
 */
public class MovieDirectorRelationFactoryTest extends EdgeFactoryTest {

	/*
	 * (non-Javadoc)
	 * 
	 * @see factory.edge.EdgeFactoryTest#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		this.factory = new MovieDirectorRelationFactory();
	}

	@Override
	@Test
	public void testCreateEdge() {
		MovieDirectorRelation edge = (MovieDirectorRelation) factory.createEdge("123",
				Arrays.asList(Movie.wrap("movie", new String[] { "1998", "China", "8.5" }),
						Director.wrap("person", new String[] { "M", "32" })),
				-1);
		assertTrue(null != edge);
		assertTrue(edge.getLabel().equals("123"));
		assertTrue(edge.getWeight() == -1);
		assertTrue(edge.containVertex(Movie.wrap("movie", new String[] { "1998", "China", "8.5" })));
		assertTrue(edge.containVertex(Director.wrap("person", new String[] { "M", "32" })));

		edge = (MovieDirectorRelation) factory.createEdge("123",
				Arrays.asList(Movie.wrap("movie", new String[] { "1998", "China", "8.5" })), -1);
		assertTrue(edge == null);
	}

}
