package factory.vertex;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import vertex.Word;

public class WordVertexFactoryTest extends VertexFactoryTest {

	@Before
	public void setUp() throws Exception {
		this.factory = new WordVertexFactory();
	}
	
	@Override
	@Test
	public void testCreateVertex() {
		Word word = (Word) factory.createVertex("v1", new String[] {});
		assertTrue(word.getLabel().equals("v1"));
	}

}
