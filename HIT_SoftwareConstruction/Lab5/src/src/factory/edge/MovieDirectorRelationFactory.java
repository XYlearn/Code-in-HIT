package factory.edge;

import java.util.List;

import edge.MovieDirectorRelation;
import vertex.Vertex;

public class MovieDirectorRelationFactory extends EdgeFactory {

	@Override
	public MovieDirectorRelation createEdge(String label, List<Vertex> vertices, double weight) {
		return MovieDirectorRelation.wrap(label, vertices, weight);
	}

}
