	import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 */

/**
 * @author 
 *
 */
public class FriendshipGraphTest {
	
	/**
	 * Testing Strategies
	 * addVertext : 
	 * 		covers add vertex existed and vertex not existed
	 * addEdge:
	 * 		covers add edge existed and not existed 
	 * getDistance:
	 * 		covers graph with vertex number >=5 
	 */

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}
	
	/**
	 * Test method for {@link FriendshipGraph#isFriend(Person, Person)}.
	 */
	@Test
	public void testIsFriend() {
		FriendshipGraph graph = new FriendshipGraph();
		Person kate = new Person("Kate");
		Person tom = new Person("Tom");
		Person jane = new Person("Jane");
		Person ben = new Person("Ben");
		Person ann = new Person("Ann");
		graph.addEdge(kate, tom);
		graph.addEdge(kate, jane);
		graph.addEdge(kate, ben);
		graph.addEdge(tom, jane);
		graph.addVertex(ann);
		assertEquals(true, graph.isFriend(kate, tom));
		assertEquals(false, graph.isFriend(tom, ann));
	}

	/**
	 * Test method for {@link FriendshipGraph#addVertex(Person)}.
	 */
	@Test
	public void testAddVertex() {
		FriendshipGraph graph = new FriendshipGraph();
		Person kate = new Person("Kate");
		Person tom = new Person("Tom");
		assertEquals(0, graph.addVertex(kate));
		assertEquals(1, graph.addVertex(tom));
		assertEquals(-1, graph.addVertex(kate));
	}

	/**
	 * covers add edge existed and not existed 
	 */
	@Test
	public void addEdgeTest () {
		FriendshipGraph graph = new FriendshipGraph();
		Person kate = new Person("Kate");
		Person tom = new Person("Tom");
		Person jane = new Person("Jane");
		assertEquals(true, graph.addEdge(kate, tom));
		assertEquals(true, graph.addEdge(tom, kate));
		assertEquals(true, graph.addEdge(jane, tom));
		assertEquals(true, graph.addEdge(tom, jane));
		assertEquals(false, graph.addEdge(kate, kate));
		assertEquals(false, graph.addEdge(kate, tom));
	}

	/**
	 * covers vertexes number == 5
	 */
	@Test
	public void testGetDistance() {
		FriendshipGraph graph = new FriendshipGraph();
		Person kate = new Person("Kate");
		Person tom = new Person("Tom");
		Person jane = new Person("Jane");
		Person ben = new Person("Ben");
		Person ann = new Person("Ann");
		graph.addEdge(kate, tom);
		graph.addEdge(tom, kate);
		graph.addEdge(kate, jane);
		graph.addEdge(jane, kate);
		graph.addEdge(kate, ben);
		graph.addEdge(ben, kate);
		graph.addEdge(tom, jane);
		graph.addEdge(jane, tom);
		graph.addVertex(ann);
		assertEquals(1, graph.getDistance(kate, jane));
		assertEquals(2, graph.getDistance(ben, tom));
		assertEquals(0, graph.getDistance(tom, tom));
		assertEquals(-1, graph.getDistance(ann, kate));
	}

}
