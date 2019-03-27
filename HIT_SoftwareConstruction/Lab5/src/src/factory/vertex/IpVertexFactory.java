package factory.vertex;

import vertex.IpVertex;

public abstract class IpVertexFactory extends VertexFactory {

	@Override
	public abstract IpVertex createVertex(String label, String[] args);

}
