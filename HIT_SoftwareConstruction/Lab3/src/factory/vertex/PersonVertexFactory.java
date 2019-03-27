package factory.vertex;

import vertex.Person;

public class PersonVertexFactory extends VertexFactory {

	@Override
	public Person createVertex(String label, String[] args) {
		return Person.wrap(label, args);
	}

}
