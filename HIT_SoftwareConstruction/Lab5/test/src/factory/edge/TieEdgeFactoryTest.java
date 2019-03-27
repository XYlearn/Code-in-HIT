package factory.edge;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;

import edge.Tie;
import vertex.Person;

public abstract class TieEdgeFactoryTest extends EdgeFactoryTest {

	@Before
	public void setUp() throws Exception {
	}

	public void testCreateEdge() {
		Tie edge = (Tie) factory.createEdge("123", Arrays.asList(Person.wrap("p1", new String[] { "M", "16" }),
				Person.wrap("p2", new String[] { "F", "24" })), 0.5);
		assertTrue(edge != null);
		assertTrue(edge.getLabel().equals("123"));
		assertTrue(edge.getWeight() == 0.5);
		assertTrue(edge.sourceVertices().contains(Person.wrap("p1", new String[] { "M", "16" })));
		assertTrue(edge.targetVertices().contains(Person.wrap("p2", new String[] { "F", "24" })));
		
		edge = (Tie) factory.createEdge("123", Arrays.asList(Person.wrap("p1", new String[] { "M" }),
				Person.wrap("p2", new String[] { "F", "24" })), 0.5);
		assertTrue(edge == null);
	}
}
