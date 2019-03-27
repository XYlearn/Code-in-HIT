package edge;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import vertex.Person;
import vertex.Vertex;

public class CommentTieTest extends TieTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
		v1 = Person.wrap("M1", new String[] {"M", "35"});
		v2 = Person.wrap("F2", new String[] {"F", "23"});
		v3 = Person.wrap("M3", new String[] {"M", "35"});
		v4 = Person.wrap("F2", new String[] {"F", "23"});
		e1024 = edgeInstance("12", Arrays.asList(v1, v2), 0.1);
		e1025 = edgeInstance("12", Arrays.asList(v1, v4), 0.1);
	}

	@Test
	public void testWrapVertexVertexDouble() {
		assertTrue(null != CommentTie.wrap("e1", Arrays.asList(v1, v2), 0.4));
		assertTrue(null == CommentTie.wrap("e2", Arrays.asList(v1, v2), 1.5));
		assertTrue(null == CommentTie.wrap("e3", Arrays.asList(v1, v2), 0));
		assertTrue(null == CommentTie.wrap("e4", Arrays.asList(v1, v1), 0.4));
	}

	@Override
	protected CommentTie edgeInstance(String label, List<Vertex> vertices, double weight) {
		return CommentTie.wrap(label, vertices, weight);
	}
	
	@Override
	protected Person vertexInstance(String label, String[] args) {
		if(args.length == 0)
			args = new String[] {"F", "23"};
		return Person.wrap(label, args);
	}
	
	@Override
	public void testWrap() {
		
	}

}
