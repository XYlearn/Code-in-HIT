package factory.vertex;

import vertex.Actor;

public class ActorVertexFactory extends PersonVertexFactory {
	@Override
	public Actor createVertex(String label, String[] args) {
		return Actor.wrap(label, args);
	}
}
