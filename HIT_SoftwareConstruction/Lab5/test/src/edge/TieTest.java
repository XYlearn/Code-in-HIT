package edge;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import vertex.Vertex;
import vertex.Word;

public abstract class TieTest extends EdgeTest {

	/**
	 * Testing Strategies
	 * 
	 * for setWeight: covers valid weight and invalid weight
	 * 
	 * for valid: covers valid weight and invalid weight, valid vertices size and
	 * invalid vertices size
	 */

	@Before
	public void setUp() throws Exception {
		super.setUp();
		e1 = edgeInstance("12", Arrays.asList(v1, v2), 0.3);
	}
	
	@Override
	abstract protected Edge edgeInstance(String label, List<Vertex> vertices, double weight);

	@Test
	public void testSetWeight() {
		Edge tie = e1;
		assertTrue(null != tie);
		assertFalse(tie.setWeight(2));
		assertFalse(tie.setWeight(-1));
		assertTrue(tie.getWeight() == 0.3);
	}

	@Test
	public void testValid() {
		Edge tie = e1;
		assertTrue(tie.valid());
		tie = new Tie("1", 0.5);
		assertFalse(tie.valid());
	}

	@Test
	public void testWrap() {
		Edge tie = e1;
		assertTrue(tie.valid() && tie instanceof FriendTie);
		tie = Tie.wrap(ForwardTie.class, "test", Arrays.asList(Word.wrap("1"), Word.wrap("2")), 0.3);
		assertTrue(tie.valid() && tie instanceof ForwardTie);
		tie = Tie.wrap(CommentTie.class, "test", Arrays.asList(Word.wrap("1"), Word.wrap("2")), 0.3);
		assertTrue(tie.valid() && tie instanceof CommentTie);
		
	}

}
