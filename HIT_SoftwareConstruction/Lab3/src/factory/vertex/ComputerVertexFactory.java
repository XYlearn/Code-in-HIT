package factory.vertex;

import vertex.Computer;
import vertex.IpVertex;

public class ComputerVertexFactory extends IpVertexFactory {

	@Override
	public IpVertex createVertex(String label, String[] args) {
		return Computer.wrap(label, args);
	}

}
