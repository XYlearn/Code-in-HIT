import static org.junit.Assert.*;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import graph.Graph;

public class RouteGraphTest {
	
	/**
     * Testing Strategies 
     * 
     * For RouteGraphGraph#add(Object)
     * covers: 
     *  vertex in graph
     *  vertex not in graph
     * 
     * For RouteGraph#set(Object, Object, int)
     * covers:
     * 	set edge not existed with weight 0
     *  set edge not existed with positive weight 
     * 	set edge existed with weight 0
     * 	set edge existed with weight not 0
     * 
     * For RouteGraph#remove(Object)
     * covers:
     * 	remove vertex existed
     * 	remove vertex not existed
     *  remove vertex has edges
     *  remove vertex has no edge
     *  
     * For RouteGraph#vertices(Object)
     * covers:
     * 	graph empty
     * 	graph has 1 vertex
     *  graph has 2 or more vertex
     *  
     * For RouteGraph#sources(Object)
     * covers:
     *  no edge linked to target
     *  one edge linked to target
     *  more than one edge linked to target
     *  
     * For RouteGraph#targets(Object)
     * covers:
     *  link to 0 vertex
     * 	link to 1 vertex
     * 	link to more than 1 vertex
     * 
     * For clone
     * covers:
     * 	graph has 0, 1, more than 2 vertices
     * 	graph has 0, 1, more than 2 edges
     * 
     */
	
	private static StopTime stoptime1 = new StopTime("stop1", 1, 1, 1);
	private static StopTime stoptime2 = new StopTime("stop2", 2, 2, 2);
	private static StopTime stoptime3 = new StopTime("stop3", 3, 3, 3);
	private static StopTime stoptime4 = new StopTime("stop4", 4, 4, 4);
	
	public RouteGraph emptyInstance() {
		return new RouteGraph();
	}

	@Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
	
	/**
     * Testing {@link Graph#add(Object)}
     * covers add vertex : already in graph
     * 					 : not in graph
     */	
	@Test
	public void testAdd() {
		RouteGraph graph = emptyInstance();
    	assertTrue("expected return true", graph.add(stoptime1));
    	assertFalse("expected return false", graph.add(stoptime1));
	}

	/**
     * Testing {@link RouteGraph#set(Object, Object, int)}
     * covers:
     * 	set edge not existed with weight 0
     *  set edge not existed with positive weight 
     * 	set edge existed with weight 0
     * 	set edge existed with weight not 0
     */
	@Test
	public void testSet() {
		RouteGraph graph = emptyInstance();
    	graph.add(stoptime1);
    	graph.add(stoptime2);
    	assertEquals("expected 0", graph.set(stoptime1, stoptime2, 0), 0);
    	assertEquals("expected 0", graph.set(stoptime1, stoptime2, 1), 0);
    	assertEquals("expected 1", graph.set(stoptime1, stoptime2, 2), 1);
    	assertEquals("expected 2", graph.set(stoptime1, stoptime2, 0), 1);
	}

	/**
     * Testing {@link RouteGraph#remove(Object)}
     * covers:
     * 	remove vertex existed
     * 	remove vertex not existed
     *  remove vertex has no edge
     *  remove vertex existed
     * 	remove vertex not existed
     *  remove vertex has edges
     */
	@Test
	public void testRemove() {
		RouteGraph graph = emptyInstance();
    	graph.add(stoptime1);
    	assertTrue("expected return true", graph.remove(stoptime1));
    	assertFalse("expected return false", graph.remove(stoptime1));
    	graph.add(stoptime1);
    	graph.add(stoptime2);
    	graph.set(stoptime1, stoptime2, 1);
    	assertTrue("expected return true", graph.remove(stoptime1));
    	assertTrue("expected remove edge", graph.sources(stoptime2).isEmpty());
	}

	/**
     * Testing {@link RouteGraph#vertices()}
     * covers:
     * 	graph has 0 vertex
     * 	graph has 1 vertex
     *  graph has more than 1 vertex
     */
	@Test
	public void testVertices() {
		assertEquals("expected new graph to have no vertices",
                Collections.emptySet(), emptyInstance().vertices());
		RouteGraph graph = emptyInstance();
    	graph.add(stoptime1);
    	assertTrue("expected set with one spec elemet",graph.vertices().contains(stoptime1) &&
    			graph.vertices().size() == 1);
    	graph.add(stoptime2);
    	assertTrue("expected set with two spec elemets", graph.vertices().contains(stoptime2) && 
    			graph.vertices().contains(stoptime2) && graph.vertices().size() == 2);
	}

	/**
     * Testing {@link RouteGraph#sources(Object)}
     * covers:
     *  no edge linked to target
     *  one edge linked to target
     *  more than one edge linked to target
     */
	@Test
	public void testSources() {
		RouteGraph graph = emptyInstance();
    	graph.add(stoptime1);
    	graph.add(stoptime2);
    	graph.add(stoptime3);
    	graph.add(stoptime4);
    	assertTrue("expected empty result", graph.sources(stoptime1).size()==0);
    	graph.set(stoptime1, stoptime2, 1);
    	assertTrue("expected result has 1 element", graph.sources(stoptime2).size()==1);
    	graph.set(stoptime3, stoptime2, 1);
    	graph.set(stoptime4, stoptime2, 3);
    	assertTrue("expected result has 3 elements", graph.sources(stoptime2).size()==3);
	}

	/**
     * Testing {@link RouteGraph#targets(Object)}
     * covers:
     *  link to 0 vert
     * 	link to 1 vert
     * 	link to more than 1 vert
     */
	@Test
	public void testTargets() {
		RouteGraph graph = emptyInstance();
    	graph.add(stoptime1);
    	graph.add(stoptime2);
    	graph.add(stoptime3);
    	graph.add(stoptime4);
    	
    	Map<StopTime, Integer> res = graph.targets(stoptime1);
    	assertTrue("expected empty result", res.size()==0);
    	
    	graph.set(stoptime1, stoptime2, 1);
    	res = graph.targets(stoptime1);
    	assertTrue("expected result has 1 element", res.size()==1 && 
    			res.containsKey(stoptime2) && res.get(stoptime2) == 1);
    	
    	graph.set(stoptime1, stoptime3, 2);
    	graph.set(stoptime1, stoptime4, 3);
    	res = graph.targets(stoptime1);
    	assertTrue("expected result has 3 elements", res.size()==3 &&
    			res.containsKey(stoptime3) && res.containsKey(stoptime4)
    			&& res.get(stoptime3)==2 && res.get(stoptime4)==3);
	}

	/**
	 * Testing {@link RouteGraph#clone()}
	 * covers:
     * 	graph has 0, 1, more than 2 vertices
     * 	graph has 0, 1, more than 2 edges
	 */
	@Test
	public void testClone() {
		RouteGraph graph = emptyInstance();
		assertTrue(graph.clone().vSize() == 0);
		assertTrue(graph.clone().eSize() == 0);
		
		graph.add(stoptime1);
		assertTrue(graph.clone().vSize() == 1);
		assertTrue(graph.clone().eSize() == 0);
		
		graph.add(stoptime2);
		assertTrue(graph.clone().vSize() == 2);
		assertTrue(graph.clone().eSize() == 0);
		
		graph.set(stoptime1, stoptime2, 1);
		assertTrue(graph.clone().vSize() == 2);
		assertTrue(graph.clone().eSize() == 1);
		
		graph.add(stoptime2);
		graph.set(stoptime1, stoptime3, 1);
		assertTrue(graph.clone().vSize() == 3);
		assertTrue(graph.clone().eSize() == 2);
		Set<StopTime> vertices = graph.vertices();
		assertTrue(vertices.contains(stoptime1) && vertices.contains(stoptime2) && 
				vertices.contains(stoptime3));
		Map<StopTime, Integer> targets = graph.targets(stoptime1);
		assertTrue(targets.containsKey(stoptime2) && targets.containsKey(stoptime3));
	}

}
