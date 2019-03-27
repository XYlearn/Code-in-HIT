package factory.edge;

import java.util.List;

import edge.MovieActorRelation;
import vertex.Vertex;

public class MovieActorRelationFactory extends EdgeFactory {

	@Override
	public MovieActorRelation createEdge(String label, List<Vertex> vertices, double weight) {
		return MovieActorRelation.wrap(label, vertices, weight);
	}

}
