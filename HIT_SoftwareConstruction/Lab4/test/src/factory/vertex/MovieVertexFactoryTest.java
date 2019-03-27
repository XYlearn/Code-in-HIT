package factory.vertex;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import vertex.Movie;

public class MovieVertexFactoryTest extends VertexFactoryTest {

	@Before
	public void setUp() throws Exception {
		this.factory = new MovieVertexFactory();
	}

	@Override
	@Test
	public void testCreateVertex() {
		Movie movie = (Movie) factory.createVertex("movie", new String[] {"1998", "China", "5.9"});
		assertTrue(movie.getLabel().equals("movie"));
		assertTrue(movie.getReleaseDate() == 1998);
		assertTrue(movie.getCountry().equals("China"));
		assertTrue(movie.getGrade() == (float)5.9);
		
		movie = (Movie) factory.createVertex("movie", new String[] {"2900", "China", "5.9"});
		assertTrue(movie == null);
		
		movie = (Movie) factory.createVertex("movie", new String[] {"1800", "China", "5.9"});
		assertTrue(movie == null);
		
		movie = (Movie) factory.createVertex("movie", new String[] {"1998", "China", "-3"});
		assertTrue(movie == null);
		
		movie = (Movie) factory.createVertex("movie", new String[] {"1998", "China", "12"});
		assertTrue(movie == null);
		
		movie = (Movie) factory.createVertex("movie", new String[] {"1998.1", "China", "5.9"});
		assertTrue(movie == null);
	}

}
