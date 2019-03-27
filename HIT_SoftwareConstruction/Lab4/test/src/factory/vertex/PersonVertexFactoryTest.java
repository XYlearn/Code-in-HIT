package factory.vertex;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import vertex.Person;

public class PersonVertexFactoryTest extends VertexFactoryTest {

	@Before
	public void setUp() throws Exception {
		this.factory = new PersonVertexFactory();
	}
	
	@Override
	@Test
	public void testCreateVertex() {
		Person person = (Person) factory.createVertex("person", new String[] {"M", "32"});
		assertTrue(null != person);
		assertTrue(person.getLabel().equals("person"));
		assertTrue(person.getAge() == 32);
		assertTrue(person.isMale());
		
		person = (Person) factory.createVertex("person", new String[] {"Male", "32"});
		assertTrue(person == null);
		
		person = (Person) factory.createVertex("person", new String[] {"M", "32.5"});
		assertTrue(person == null);
		
		person = (Person) factory.createVertex("person", new String[] {"F"});
		assertTrue(person == null);
	}
}
