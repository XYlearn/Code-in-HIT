/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * An implementation of Graph.
 * 
 * <p>PS2 instructions: you MUST use the provided rep.
 */
public class ConcreteEdgesGraph<L> implements Graph<L> {
    
    private final Set<L> vertices = new HashSet<>();
    private final List<Edge<L>> edges = new ArrayList<>();
    
    /**
     * Abstraction function:
     * 	AF(g) = {e1, e2, ..., en | e is edge of graph of type {@code Edge}}}
     * 
     * Representation invariant:
     * 	don't exist edges with same source and target
     * 	e.weight > 0 for edge in graph
     * 	for all e in edges, e.source, e.target in vertices
     * 
     * Safety from rep exposure:
     * 	all fields are private and final
     * 	return copied vertices when call vertices()
     * 	elements of vertices and edges are immutable, so will not be changed in returned collections
     */
    
    public ConcreteEdgesGraph() {
		checkRep();
	}
    
    /**
     * Check representation
     * @return representation
     */
    private void checkRep() {
    	//traversal edges
    	for(Edge<L> edge : edges) {
    		assert edge.getWeight() > 0;
    		assert !edge.getSource().equals(edge.getTarget());
    		assert vertices.contains(edge.getSource());
    		assert vertices.contains(edge.getTarget());
    	}
    }
    
    @Override public boolean add(L vertex) {
    	boolean res = vertices.add(vertex);
    	checkRep();
        return res;
    }
    
    @Override public int set(L source, L target, int weight) {
    	int oldWeight = 0; // oldWeight for return
    	boolean vertexNotExist = false; // mark vertex not exists 
    	
    	// check if source exists
    	if(!vertices.contains(source)) {
    		this.add(source);
    		vertexNotExist = true;
    	}
    	// check if target exists
    	if(!vertices.contains(target)) {
    		this.add(target);
    		vertexNotExist = true;
    	}
    	
    	// both source and target exist, need to traversal edges
    	if(!vertexNotExist) {
	    	// search for edge
	        for(Iterator<Edge<L>> ite = edges.iterator(); ite.hasNext();) {
	        	Edge<L> edge = ite.next();
	        	// remove found edge and record its weight
	        	if(edge.link(source, target)) {
	        		oldWeight = edge.getWeight();
	        		ite.remove();
	        		break;
	        	}
	        }
    	}
    	
        // add new edge if weight is positive
        if(weight > 0)
        	edges.add(new Edge<L>(source, target, weight));
        
        checkRep();
        return oldWeight;
    }
    
    @Override public boolean remove(L vertex) {
    	boolean found = false;	// mark if vertex is found
    	
    	
    	found = vertices.remove(vertex);
    	if(!found)
    		return false;
    	
    	// traversal edges
    	for(Iterator<Edge<L>> ite = edges.iterator(); ite.hasNext();) {
        	Edge<L> edge = ite.next();
        	// remove edge related to vertex
        	if(edge.linkFrom(vertex) || edge.linkTo(vertex)) {
        		ite.remove();
        	}
        }
    	
    	checkRep();
    	return found;
    }
    
    @Override public Set<L> vertices() {
    	Set<L> res =new HashSet<>(vertices);
    	checkRep();
        return res;
    }
    
    @Override public Map<L, Integer> sources(L target) {
    	Map<L, Integer> srcs = new HashMap<>(); // result to return
    	
    	// traversal edges
    	for(Iterator<Edge<L>> ite = edges.iterator(); ite.hasNext();) {
        	Edge<L> edge = ite.next();
        	// find edges linking to target
        	if(edge.linkTo(target)) {
        		srcs.put(edge.getSource(), edge.getWeight());
        	}
        }
    	checkRep();
    	return srcs;
    }
    
    @Override public Map<L, Integer> targets(L source) {
    	Map<L, Integer> tgts = new HashMap<>(); // result to return
    	
    	// traversal edges
    	for(Iterator<Edge<L>> ite = edges.iterator(); ite.hasNext();) {
        	Edge<L> edge = ite.next();
        	// find edges linking from source
        	if(edge.linkFrom(source)) {
        		tgts.put(edge.getTarget(), edge.getWeight());
        	}
        }
    	checkRep();
    	return tgts;
    }
    
    @Override
    public String toString() {
    	StringBuilder stringBuilder = new StringBuilder();
    	for(L vertex : vertices) {
    		stringBuilder.append(vertex);
    		stringBuilder.append("{");
    		Map<L, Integer> tgts = targets(vertex);
    		for(Entry<L, Integer> entry : tgts.entrySet()) {
    			stringBuilder.append(new Edge<L>(vertex, entry.getKey(), entry.getValue()).toString());
    		}
    		stringBuilder.append("}; ");
    	}
    	return stringBuilder.toString();
    }
    
}

/**
 * Edge represent directed edge linking two two vertices with positive weight
 * Immutable.
 * This class is internal to the rep of ConcreteEdgesGraph.
 * 
 * <p>PS2 instructions: the specification and implementation of this class is
 * up to you.
 * @param <L>
 */
class Edge<L> {
	
    private final int weight;
    private final L source;
    private final L target;
    
    /**
     * Abstraction functions: 
     *  represent directed edge linking from source Vertex 
     *  to target Vertex with weight
     * 
     * Representation invariant:
     * 	source != target && weight > 0
     * 
     * Safety from rep exposure:
     * 	All fields are private and final, all types in rep is immutable
     * 	source and target are Ls, so are guaranteed immutable
     */
    
    
    /**
     * constructor of Edge
     * @param source vertex edge link from
     * @param target vertex edge link to
     * @param weight weight of edge
     */
    public Edge(L source, L target, int weight) {
    	this.source = source;
    	this.target = target;
    	this.weight = weight;
    }
    
    /**
     * get weight of edge
     * @return weight of edge
     */
    public int getWeight() {
    	return this.weight;
    }
    
    /**
     * get source of edge
     * @return source of edge
     */
    public L getSource() {
    	return source;
    }
    
    /**
     * get target of edge
     * @return target of edge
     */
    public L getTarget() {
    	return target;
    }
    
    /**
     * check representation
     * @return true if rep 
     */
    protected void checkRep() {
    	assert !source.equals(target);
    	assert weight > 0;
    }
    
    // TODO methods
    /**
     * check if edge link two vertices directed
     * @param source source vertex link from
     * @param target target vertex link to
     * @return true if edge link source to target; else false
     */
    public boolean link(L source, L target) {
    	return this.source.equals(source) && this.target.equals(target);
    }
    
    /**
     * check if edge link from vertex
     * @param source source vertex link from
     * @return true if edge link from vertex; else false
     */
    public boolean linkFrom(L source) {
    	return this.source.equals(source);
    }
    
    /**
     * check if edge link to vertex
     * @param target source vertex link to
     * @return true if edge link to vertex; else false
     */
    public boolean linkTo(L target) {
    	return this.target.equals(target);
    }
    
    @Override
	public String toString() {
    	StringBuilder stringBuilder = new StringBuilder();
    	stringBuilder.append("(")
    		.append(source).append("->")
    		.append(target).append(":")
    		.append(weight).append(")");
    	checkRep();
    	return stringBuilder.toString();
    }
    
}
