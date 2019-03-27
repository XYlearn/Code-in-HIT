package factory.vertex;

import vertex.Server;

public class ServerVertexFactory extends IpVertexFactory {

	@Override
	public Server createVertex(String label, String[] args) {
		return Server.wrap(label, args);
	}

}
