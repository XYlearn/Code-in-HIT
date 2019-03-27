package factory.vertex;

import vertex.Director;

public class DirectorVertexFactory extends PersonVertexFactory {
	
	@Override
	public Director createVertex(String label, String[] args) {
		return Director.wrap(label, args);
	}
}
