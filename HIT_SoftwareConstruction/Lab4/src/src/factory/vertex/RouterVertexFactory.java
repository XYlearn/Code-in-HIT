package factory.vertex;

import vertex.Router;

public class RouterVertexFactory extends IpVertexFactory {

	@Override
	public Router createVertex(String label, String[] args) {
		return Router.wrap(label, args);
	}

}
