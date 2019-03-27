/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for ConcreteVerticesGraph.
 * 
 * This class runs the GraphInstanceTest tests against ConcreteVerticesGraph, as
 * well as tests for that particular implementation.
 * 
 * Tests against the Graph spec should be in GraphInstanceTest.
 */
public class ConcreteVerticesGraphTest extends GraphInstanceTest {
    
    /*
     * Provide a ConcreteVerticesGraph for tests in GraphInstanceTest.
     */
    @Override public Graph<String> emptyInstance() {
        return new ConcreteVerticesGraph<String>();
    }
    
    /*
     * Testing ConcreteVerticesGraph...
     */
    
    /**
     * Testing strategy for ConcreteVerticesGraph.toString()
     * covers ConcreteVerticesGraph with 0, 1, more than 1 vertices
     */
    
    /**
     * Testing {@link ConcreteVerticesGraph#toString()}
     * covers ConcreteVerticesGraph with 0, 1, more than 1 vertices
     */
    @Test
    public void testConcreteVerticesGraphToString() {
    	Graph<String> graph = emptyInstance();
    	assertEquals("expected {}", graph.toString(), "{}");
    	graph.add("vertex1");
    	assertTrue("expected vertex1 in string", graph.toString().contains("vertex1"));
    	graph.add("vertex2");
    	assertTrue("expected vertex1 in string", graph.toString().contains("vertex1")
    			&& graph.toString().contains("vertex2"));
    	graph.set("vertex1", "vertex2", 1);
    	assertTrue("expected edge", graph.toString().contains("->vertex2:1"));
    }
    
    /*
     * Testing Vertex...
     */
    
    /** 
     * Testing strategy for Vertex
     * covers each methods once
     * 
     * For setMethod 
     * covers target exist and not exist
     * 
     * For other methods only test once
     * 	
     */
    
    /**
     * Testing {@link Vertex#setSource(String, int) Vertex#setTarget(String, int)}
     */
    @Test
    public void testVertexSetMethods() {
    	Vertex<String> vertex = new Vertex<String>("vertex1");
    	assertTrue("expected 0", vertex.setTarget("vertex2", 4) == 0);
    	assertTrue("expected 4", vertex.setTarget("vertex2", 0) == 4);
    	assertTrue("expected 0", vertex.setTarget("vertex2", 4) == 0);
    	
    	vertex.setSource("vertex2", 5);
    	assertTrue("expected 5", vertex.setSource("vertex2", 4) == 5);
    	assertTrue("expected 4", vertex.setSource("vertex2", 0) == 4);
    	assertTrue("expected 0", vertex.setSource("vertex2", 4) == 0);
    }
    
    /**
     * Testing {@link Vertex#getLable()} 
     * {@link Vertex#hasSource(String)} {@link Vertex#hasTarget(String)}
     * {@link Vertex#getSourceWeight(String)} {@link Vertex#getTargetWeight(String)}
     */
    @Test
    public void testVertexOtherMethods() {
    	Vertex<String> vertex = new Vertex<>("vert1");
    	assertEquals("expected correct label", vertex.getLable(), "vert1");
    	
    	vertex.setSource("vertex2",	1);
    	assertTrue("expected true", vertex.hasSource("vertex2"));
    	vertex.setTarget("vertex2", 2);
    	assertTrue("expected true", vertex.hasTarget("vertex2"));
    	
    	assertEquals("expected correct weight", vertex.getSourceWeight("vertex2"), 1);
    	assertEquals("expected correct weight", vertex.getTargetWeight("vertex2"), 2);
    }
    
}
