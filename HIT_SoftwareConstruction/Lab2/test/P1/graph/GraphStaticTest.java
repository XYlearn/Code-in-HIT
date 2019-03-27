/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;

/**
 * Tests for static methods of Graph.
 * 
 * To facilitate testing multiple implementations of Graph, instance methods are
 * tested in GraphInstanceTest.
 */
public class GraphStaticTest {
    
    // Testing strategy
    //   empty()
    //     no inputs, only output is empty graph
    //     observe with vertices()
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testEmptyVerticesEmpty() {
        assertEquals("expected empty() graph to have no vertices",
                Collections.emptySet(), Graph.empty().vertices());
    }
    
    /**
     * Testing Graph<Integer>
     */
    /**
     * Testing {@link Graph#vertices()}
     * covers:
     * 	graph has 1 vertex
     *  graph has more than 1 vertex
     */
    @Test
    public void testVerticiesNotEmpty() {
    	Graph<Integer> graph = Graph.empty();
    	graph.add(1);
    	assertTrue("expected set with one spec elemet",graph.vertices().contains(1) &&
    			graph.vertices().size() == 1);
    	graph.add(2);
    	assertTrue("expected set with two spec elemets", graph.vertices().contains(2) && 
    			graph.vertices().contains(2) && graph.vertices().size() == 2);
    }
    
    /**
     * Testing {@link Graph#add(Object)}
     * covers add vertex : already in graph
     * 					 : not in graph
     */		
    @Test
    public void testAdd() {
    	Graph<Integer> graph = Graph.empty();
    	assertTrue("expected return true", graph.add(0));
    	assertFalse("expected return false", graph.add(0));
    }
    
    /**
     * Testing {@link Graph#set(Object, Object, int)}
     * covers:
     * 	set edge not existed with weight 0
     *  set edge not existed with positive weight 
     * 	set edge existed with weight 0
     * 	set edge existed with weight not 0
     */
    @Test
    public void testSet() {
    	Graph<Integer> graph = Graph.empty();
    	graph.add(1);
    	graph.add(2);
    	assertEquals("expected 0", graph.set(1, 2, 0), 0);
    	assertEquals("expected 0", graph.set(1, 2, 1), 0);
    	assertEquals("expected 1", graph.set(1, 2, 2), 1);
    	assertEquals("expected 2", graph.set(1, 2, 0), 2);
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
    	Graph<Integer> graph = Graph.empty();
    	graph.add(0);
    	assertTrue("expected return true", graph.remove(0));
    	assertFalse("expected return false", graph.remove(0));
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
    	Graph<Integer> graph = Graph.empty();
    	graph.add(1);
    	graph.add(2);
    	graph.set(1, 2, 1);
    	assertTrue("expected return true", graph.remove(1));
    	assertTrue("expected remove edge", graph.sources(2).isEmpty());
    }

    /**
     * Testing {@link Graph#sources(Object)}
     * covers:
     *  no edge linked to target
     *  one edge linked to target
     *  more than one edge linked to target
     */
    @Test
    public void testSources() {
    	Graph<Integer> graph = Graph.empty();
    	graph.add(1);
    	graph.add(2);
    	graph.add(3);
    	graph.add(4);
    	assertTrue("expected empty result", graph.sources(1).size()==0);
    	graph.set(1, 2, 1);
    	assertTrue("expected result has 1 element", graph.sources(2).size()==1);
    	graph.set(3, 2, 1);
    	graph.set(4, 2, 3);
    	assertTrue("expected result has 3 elements", graph.sources(2).size()==3);
    }
    
    /**
     * Testing {@link Graph#targets(Object)}
     * covers:
     *  link to 0 vert
     * 	link to 1 vert
     * 	link to more than 1 vert
     */
    @Test
    public void testTarget() {
    	Graph<Integer> graph = Graph.empty();
    	graph.add(1);
    	graph.add(2);
    	graph.add(3);
    	graph.add(4);
    	
    	Map<Integer, Integer> res = graph.targets(1);
    	assertTrue("expected empty result", res.size()==0);
    	
    	graph.set(1, 2, 1);
    	res = graph.targets(1);
    	assertTrue("expected result has 1 element", res.size()==1 && 
    			res.containsKey(2) && res.get(2) == 1);
    	
    	graph.set(1, 3, 2);
    	graph.set(1, 4, 3);
    	res = graph.targets(1);
    	assertTrue("expected result has 3 elements", res.size()==3 &&
    			res.containsKey(3) && res.containsKey(4)
    			&& res.get(3)==2 && res.get(4)==3);
    }
    @Test
    public void testVertexLabelInteger() {
    	Graph<Integer> graph = Graph.empty();
    	assertTrue("expected true", graph.add(1));
    	graph.add(2);
    }
    
}
