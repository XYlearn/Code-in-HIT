/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for ConcreteEdgesGraph.
 * 
 * This class runs the GraphInstanceTest tests against ConcreteEdgesGraph, as
 * well as tests for that particular implementation.
 * 
 * Tests against the Graph spec should be in GraphInstanceTest.
 */
public class ConcreteEdgesGraphTest extends GraphInstanceTest {
    
    /*
     * Provide a ConcreteEdgesGraph for tests in GraphInstanceTest.
     */
    @Override public Graph<String> emptyInstance() {
        return new ConcreteEdgesGraph<String>();
    }
    
    /*
     * Testing ConcreteEdgesGraph...
     */
    
    /**
     * Testing Strategies
     * 
     * For ConcreteEdgeGraph#toString
     * 	covers : empty graph
     * 			 graph with only vertices
     * 			 graph with vertices and edges
     * {@link ConcreteEdgesGraph#toString}
     * 
     * For Edge
     * 	test once for each method
     */
    
    
    /**
     * Testing ConcreteEdgeGraph#toString
     */
    @Test
    public void testConcretEdgeGraphToString() {
    	Graph<String> graph = emptyInstance();
    	assertEquals("expected empty string", graph.toString(), "");
    	graph.add("vert1");
    	System.out.println(graph.toString());
    	assertTrue("expected non-empty string", graph.toString().contains("vert1"));
    	graph.add("vert2");
    	graph.set("vert1", "vert2", 1);
    	System.out.println(graph.toString());
    	assertTrue("expected non-empty string", graph.toString().contains("vert2")
    			&& graph.toString().contains("vert1->vert2"));
    }
    
    /*
     * Testing Edge...
     */
    @Test
    public void testEdgeMethods() {
    	Edge<String> edge = new Edge<String>("vert1", "vert2", 7);
    	assertEquals("expected vert1", edge.getSource(), "vert1");
    	assertEquals("expected vert1", edge.getTarget(), "vert2");
    	assertEquals("expected 7", edge.getWeight(), 7);
    	assertTrue("expected true", edge.link("vert1", "vert2"));
    	assertFalse("expected false", edge.link("vert1", "vert1"));
    	assertTrue("expected true", edge.linkFrom("vert1"));
    	assertTrue("expected true", edge.linkTo("vert2"));
    }
    
    @Test
    public void testEdgeToString() {
    	Edge<String> edge = new Edge<>("vert1", "vert2", 7);
    	assertTrue("expected right string", edge.toString().contains("(vert1->vert2:7)"));
    }
}
