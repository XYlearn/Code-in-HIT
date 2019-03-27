/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;

/**
 * Tests for instance methods of Graph.
 * 
 * <p>PS2 instructions: you MUST NOT add constructors, fields, or non-@Test
 * methods to this class, or change the spec of {@link #emptyInstance()}.
 * Your tests MUST only obtain Graph instances by calling emptyInstance().
 * Your tests MUST NOT refer to specific concrete implementations.
 */
public abstract class GraphInstanceTest {
    
    /**
     * Testing Strategies 
     * 
     * For Graph#add(Object)
     * covers: 
     *  vertex in graph
     *  vertex not in graph
     * 
     * For Graph#set(Object, Object, int)
     * covers:
     * 	set edge not existed with weight 0
     *  set edge not existed with positive weight 
     * 	set edge existed with weight 0
     * 	set edge existed with weight not 0
     * 
     * For Graph#remove(Object)
     * covers:
     * 	remove vertex existed
     * 	remove vertex not existed
     *  remove vertex has edges
     *  remove vertex has no edge
     *  
     * For Graph#vertices(Object)
     * covers:
     * 	graph empty
     * 	graph has 1 vertex
     *  graph has 2 or more vertex
     *  
     * For Graph#sources(Object)
     * covers:
     *  no edge linked to target
     *  one edge linked to target
     *  more than one edge linked to target
     *  
     * For Graph#targets(Object)
     * covers:
     *  link to 0 vert
     * 	link to 1 vert
     * 	link to more than 1 vert
     */
    
    /**
     * Overridden by implementation-specific test classes.
     * 
     * @return a new empty graph of the particular implementation being tested
     */
    public abstract Graph<String> emptyInstance();
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
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
    	Graph<String> graph = emptyInstance();
    	graph.add("vert1");
    	assertTrue("expected set with one spec elemet",graph.vertices().contains("vert1") &&
    			graph.vertices().size() == 1);
    	graph.add("vert2");
    	assertTrue("expected set with two spec elemets", graph.vertices().contains("vert2") && 
    			graph.vertices().contains("vert2") && graph.vertices().size() == 2);
    }
    
    /**
     * Testing {@link Graph#add(Object)}
     * covers add vertex : already in graph
     * 					 : not in graph
     */		
    @Test
    public void testAdd() {
    	Graph<String> graph = emptyInstance();
    	assertTrue("expected return true", graph.add("vertex"));
    	assertFalse("expected return false", graph.add("vertex"));
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
    	Graph<String> graph = emptyInstance();
    	graph.add("vert1");
    	graph.add("vert2");
    	assertEquals("expected 0", graph.set("vert1", "vert2", 0), 0);
    	assertEquals("expected 0", graph.set("vert1", "vert2", 1), 0);
    	assertEquals("expected 1", graph.set("vert1", "vert2", 2), 1);
    	assertEquals("expected 2", graph.set("vert1", "vert2", 0), 2);
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
    	Graph<String> graph = emptyInstance();
    	graph.add("vert");
    	assertTrue("expected return true", graph.remove("vert"));
    	assertFalse("expected return false", graph.remove("vert"));
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
    	Graph<String> graph = emptyInstance();
    	graph.add("vert1");
    	graph.add("vert2");
    	graph.set("vert1", "vert2", 1);
    	assertTrue("expected return true", graph.remove("vert1"));
    	assertTrue("expected remove edge", graph.sources("vert2").isEmpty());
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
    	Graph<String> graph = emptyInstance();
    	graph.add("vertex1");
    	graph.add("vertex2");
    	graph.add("vertex3");
    	graph.add("vertex4");
    	assertTrue("expected empty result", graph.sources("vertex1").size()==0);
    	graph.set("vertex1", "vertex2", 1);
    	assertTrue("expected result has 1 element", graph.sources("vertex2").size()==1);
    	graph.set("vertex3", "vertex2", 1);
    	graph.set("vertex4", "vertex2", 3);
    	assertTrue("expected result has 3 elements", graph.sources("vertex2").size()==3);
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
    	Graph<String> graph = emptyInstance();
    	graph.add("vertex1");
    	graph.add("vertex2");
    	graph.add("vertex3");
    	graph.add("vertex4");
    	
    	Map<String, Integer> res = graph.targets("vertex1");
    	assertTrue("expected empty result", res.size()==0);
    	
    	graph.set("vertex1", "vertex2", 1);
    	res = graph.targets("vertex1");
    	assertTrue("expected result has 1 element", res.size()==1 && 
    			res.containsKey("vertex2") && res.get("vertex2") == 1);
    	
    	graph.set("vertex1", "vertex3", 2);
    	graph.set("vertex1", "vertex4", 3);
    	res = graph.targets("vertex1");
    	assertTrue("expected result has 3 elements", res.size()==3 &&
    			res.containsKey("vertex3") && res.containsKey("vertex4")
    			&& res.get("vertex3")==2 && res.get("vertex4")==3);
    }
}
