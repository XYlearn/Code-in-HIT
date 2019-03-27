package factory.vertex;

import vertex.Movie;

public class MovieVertexFactory extends VertexFactory {

	@Override
	public Movie createVertex(String label, String[] args) {
		return Movie.wrap(label, args);
	}

}
