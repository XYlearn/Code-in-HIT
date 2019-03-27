package vertex;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PersonTest extends VertexTest {

	/**
	 * Testing Strategies
	 * 
	 * for fillVertexInfo: covers valid age, invalid age, valid sex, invalid sex
	 * 
	 * for isMale: covers male and female
	 * 
	 * for getAge: test the result
	 * 
	 * for 
	 */

	@Before
	public void setUp() throws Exception {
		vertex = vertexInstance("1", new String[] {"F", "16"});
		vertex1 = vertexInstance("1", new String[] {"F", "16"});
		vertex2 = vertexInstance("2", new String[] {"M", "18"});
	}
	
	@Override
	protected Person vertexInstance(String label, String[] args) {
		return Person.wrap(label, args);
	}

	/**
	 * Testing {@link Person#fillVertexInfo(String[])}. covers valid age, invalid
	 * age, valid sex, invalid sex
	 */
	@Test
	public void testFillVertexInfo() {
		Person person = vertexInstance("x", new String[] {"M", "32"});
		assertTrue(null != person);
		
		person = vertexInstance("x", new String[] {"F", "32"});
		assertTrue(null != person);
		
		person = vertexInstance("x", new String[] {"FM", "32"});
		assertTrue(null == person);
		
		person = vertexInstance("x", new String[] {"M", "-3"});
		assertTrue(null == person);
		
		person = vertexInstance("x", new String[] {"F", "A"});
		assertTrue(null == person);
		
		person = vertexInstance("x", new String[] {"M", "32"});
		person.fillVertexInfo(new String[] {"F", "16"});
		assertTrue(person.isMale());
		assertTrue(person.getAge() == 16);
	}

	@Test
	public void testIsMale() {
		assertFalse(((Person)vertex1).isMale());
		assertTrue(((Person)vertex2).isMale());
	}

	@Test
	public void testGetAge() {
		assertTrue(((Person)vertex).getAge() == 16);
	}

	@Test
	public void testSetAge() {
		assertFalse(((Person)vertex).setAge(-3));
		assertTrue(((Person)vertex).setAge(0));
	}

	@Test
	public void testWrap() {
		assertTrue(null != Person.wrap("x", new String[] {"M", "3"}));
		Person person = Person.wrap("x", true, 32);
		assertTrue(null != person);
		assertTrue(person.isMale());
		assertTrue(person.getAge() == 32);
	}

}
