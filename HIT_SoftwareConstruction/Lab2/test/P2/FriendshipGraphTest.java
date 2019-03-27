import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import graph.Graph;

/**
 * @author 
 *
 */
public class FriendshipGraphTest {
	
	/**
	 * Testing Strategies
	 * addVertext, add : 
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
	
	@Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
	
	/**
	 * Test method for {@link FriendshipGraph#addVertex(Person)}.
	 */
	@Test
	public void testAddVertex() {
		FriendshipGraph graph = new FriendshipGraph();
		Person kate = new Person("Kate");
		Person tom = new Person("Tom");
		assertEquals(true, graph.addVertex(kate));
		assertEquals(true, graph.addVertex(tom));
		assertEquals(false, graph.addVertex(kate));
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
	
	/**
     * Testing Strategies for Override methods
     * 
     * For FriendshipGraph#add(Object)
     * covers: 
     *  vertex in graph
     *  vertex not in graph
     * 
     * For FriendshipGraph#set(Object, Object, int)
     * covers:
     * 	set edge not existed with weight 0
     *  set edge not existed with positive weight 
     * 	set edge existed with weight 0
     * 	set edge existed with weight not 0
     * 
     * For FriendshipGraph#remove(Object)
     * covers:
     * 	remove vertex existed
     * 	remove vertex not existed
     *  remove vertex has edges
     *  remove vertex has no edge
     *  
     * For FriendshipGraph#vertices(Object)
     * covers:
     * 	graph empty
     * 	graph has 1 vertex
     *  graph has 2 or more vertex
     *  
     * For FriendshipGraph#sources(Object)
     * covers:
     *  no edge linked to target
     *  one edge linked to target
     *  more than one edge linked to target
     *  
     * For FriendshipGraph#targets(Object)
     * covers:
     *  link to 0 vert
     * 	link to 1 vert
     * 	link to more than 1 vert
     */
    
	public static FriendshipGraph emptyInstance() {
		return new FriendshipGraph();
	}
	
	public static Person newPerson(String name) {
		return new Person(name);
	}
       
    /**
     * Testing {@link Graph#vertices()}
     * covers graph empty
     */
    @Test
    public void testInitialVerticesEmpty() {
        assertEquals("expected new graph to have no vertices",
                Collections.emptySet(), emptyInstance().vertices());
    }
    
    /**
     * Testing {@link Graph#vertices()}
     * covers:
     * 	graph has 1 vertex
     *  graph has more than 1 vertex
     */
    @Test
    public void testVerticiesNotEmpty() {
    	FriendshipGraph graph = emptyInstance();
    	graph.add(newPerson("person1"));
    	assertTrue("expected set with one spec elemet",graph.vertices().contains(newPerson("person1")) &&
    			graph.vertices().size() == 1);
    	graph.add(newPerson("person2"));
    	assertTrue("expected set with two spec elemets", graph.vertices().contains(newPerson("person2")) && 
    			graph.vertices().contains(newPerson("person2")) && graph.vertices().size() == 2);
    }
    
    /**
     * Testing {@link Graph#add(Object)}
     * covers add vertex : already in graph
     * 					 : not in graph
     */		
    @Test
    public void testAdd() {
    	FriendshipGraph graph = emptyInstance();
    	assertTrue("expected return true", graph.add(newPerson("person")));
    	assertFalse("expected return false", graph.add(newPerson("person")));
    }
    
    /**
     * Testing {@link Graph#remove(Object)}
     * covers:
     * 	remove vertex existed
     * 	remove vertex not existed
     *  remove vertex has no edge
     */
    @Test
    public void testRemoveWithoutEdge() {
    	FriendshipGraph graph = emptyInstance();
    	graph.add(newPerson("person"));
    	assertTrue("expected return true", graph.remove(newPerson("person")));
    	assertFalse("expected return false", graph.remove(newPerson("person")));
    }
    
    /**
     * Testing {@link Graph#remove(Object)}
     * covers:
     * 	remove vertex existed
     * 	remove vertex not existed
     *  remove vertex has edges
     */
    @Test
    public void testRemoveWithEdge() {
    	FriendshipGraph graph = emptyInstance();
    	graph.add(newPerson("person1"));
    	graph.add(newPerson("person2"));
    	graph.addEdge(newPerson("person1"), newPerson("person2"));
    	assertTrue("expected return true", graph.remove(newPerson("person1")));
    	assertTrue("expected remove edge", graph.fsTargets(newPerson("person2")).isEmpty());
    }

    /**
     * Testing {@link Graph#sources(Object)}
     * covers:
     *  no edge linked to target
     *  one edge linked to target
     *  more than one edge linked to target
     */
    @Test
    public void testFsSources() {
    	FriendshipGraph graph = emptyInstance();
    	graph.add(newPerson("person1"));
    	graph.add(newPerson("person2"));
    	graph.add(newPerson("person3"));
    	graph.add(newPerson("person4"));
    	assertTrue("expected empty result", graph.fsSources(newPerson("person2")).size()==0);
    	graph.addEdge(newPerson("person1"), newPerson("person2"));
    	assertTrue("expected result has 1 element", graph.fsSources(newPerson("person2")).size()==1);
    	graph.addEdge(newPerson("person3"), newPerson("person2"));
    	graph.addEdge(newPerson("person4"), newPerson("person2"));
    	assertTrue("expected result has 3 elements", graph.fsSources(newPerson("person2")).size()==3);
    }
    
    /**
     * Testing {@link Graph#targets(Object)}
     * covers:
     *  link to 0 vert
     * 	link to 1 vert
     * 	link to more than 1 vert
     */
    @Test
    public void testFsTarget() {
    	FriendshipGraph graph = emptyInstance();
    	graph.add(newPerson("person2"));
    	graph.add(newPerson("person3"));
    	graph.add(newPerson("person4"));
    	
    	Set<Person> res = graph.fsTargets(newPerson("person2"));
    	assertTrue("expected empty result", res.size()==0);
    	
    	graph.addEdge(newPerson("person2"), newPerson("person1"));
    	res = graph.fsTargets(newPerson("person2"));
    	assertTrue("expected result has 1 element", res.size()==1 && 
    			res.contains(newPerson("person1")));
    	
    	graph.addEdge(newPerson("person2"), newPerson("person3"));
    	graph.addEdge(newPerson("person2"), newPerson("person4"));
    	res = graph.fsTargets(newPerson("person2"));
    	assertTrue("expected result has 3 elements", res.size()==3 &&
    			res.contains(newPerson("person3")) && res.contains(newPerson("person4")));
    }

}
