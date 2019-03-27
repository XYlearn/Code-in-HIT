package factory.edge;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import edge.WordNeighborhood;
import vertex.Word;

public class WordNeighborhoodFactoryTest extends EdgeFactoryTest {

	@Before
	public void setUp() throws Exception {
		this.factory = new WordNeighborhoodFactory();
	}

	@Override
	@Test
	public void testCreateEdge() {
		WordNeighborhood edge = (WordNeighborhood) factory.createEdge("123",
				Arrays.asList(Word.wrap("w1"), Word.wrap("w2")), 2);
		assertTrue(edge.getLabel().equals("123"));
		assertTrue(edge.getWeight() == 2);
		assertTrue(edge.containVertex(Word.wrap("w1")));
		assertTrue(edge.containVertex(Word.wrap("w2")));
		
		edge = (WordNeighborhood) factory.createEdge("123",
				Arrays.asList(Word.wrap("w1"), Word.wrap("w2")), -2);
		assertTrue(edge == null);
	}

}
