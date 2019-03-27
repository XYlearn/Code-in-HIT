package factory.vertex;

import vertex.Word;

public class WordVertexFactory extends VertexFactory {

	@Override
	public Word createVertex(String label, String[] args) {
		return Word.wrap(label, args);
	}

}
