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
public class ConcreteVerticesGraph<L> implements Graph<L> {
    
    private final List<Vertex<L>> vertices = new ArrayList<>();
    
    /**
     * Abstraction function:
     * 	AF(g) = {v1, v2, .. vn | v is vertex of gragh of type {@code Vertex} }
     * 
     * Representation invariant:
     * 	for all v1, v2 in vertices: v1 != v2
     * 
     * Safety from rep exposure:
     * 	field is private and final
     * 	function behave as generators return copied value or immutable value
     */
    
    public ConcreteVerticesGraph() {
		checkRep();
	}
    
    protected void checkRep() {
    	HashSet<L> set = new HashSet<>();
    	for(Vertex<L> vertex : vertices)
    		set.add(vertex.getLable());
    	assert set.size() == vertices.size();
    }
    
    private boolean hasVertex(L vertex) {
    	for(Vertex<L> vert : vertices) {
    		if(vert.getLable().equals(vertex))
    			return true;
    	}
    	return false;
    }
    
    private Vertex<L> getVertex(L vertex) {
    	for(Vertex<L> vert : vertices) {
    		if(vert.getLable().equals(vertex))
    			return vert;
    	}
    	return null;
    }
    
    @Override public boolean add(L vertex) {
    	boolean exist = hasVertex(vertex);
    	if(!exist)
    		vertices.add(new Vertex<>(vertex));
    	checkRep();
    	return !exist;
    }
    
    @Override public int set(L source, L target, int weight) {
    	Vertex<L> svert = getVertex(source);
    	Vertex<L> tvert = getVertex(target);
    	int oldWeight = 0;
    	
    	// add vertices if not exist
    	if(null == svert) {
    		svert = new Vertex<>(source);
    	}
    	if(null == tvert) {
    		tvert = new Vertex<>(target);
    	}
    	
    	oldWeight = svert.setTarget(target, weight);
    	tvert.setSource(source, weight);
    	
    	checkRep();
    	return oldWeight;
    }
    
    @Override public boolean remove(L vertex) {
    	Iterator<Vertex<L>> ite = vertices.iterator();
    	// tarversal vertices
    	while (ite.hasNext()) {
    		Vertex<L> vert = ite.next();
    		if(vert.getLable().equals(vertex)) {
    			// remove related edges
    			for(L target : vert.targets().keySet()) {
    				getVertex(target).setSource(vertex, 0);
    			}
    			for(L source : vert.sources().keySet()) {
    				getVertex(source).setTarget(vertex, 0);
    			}
    			ite.remove();
    			checkRep();
    			return true;
    		}
    	}
    	checkRep();
    	// return false if not found
    	return false;
    }
    
    @Override public Set<L> vertices() {
        Set<L> res = new HashSet<>();
        // traversal and add labels
        for(Vertex<L> vert : vertices) {
        	res.add(vert.getLable());
        }
        checkRep();
        return res;
    }
    
    @Override public Map<L, Integer> sources(L target) {
        Map<L, Integer> res = new HashMap<>();
        // get all sources of vertices
        for(Vertex<L> vert : vertices) {
        	res.putAll(vert.sources());
        }
        return res;
    }
    
    @Override public Map<L, Integer> targets(L source) {
        Map<L, Integer> res = new HashMap<>();
     // get all targets of vertices
        for(Vertex<L> vert : vertices) {
        	res.putAll(vert.targets());
        }
        return res;
    }
    
    @Override public String toString() {
    	StringBuilder stringBuilder = new StringBuilder();
    	stringBuilder.append("{ ");
    	for(Vertex<L> vert : vertices) {
    		stringBuilder.append(vert.toString());
    		stringBuilder.append(",");
    	}
    	stringBuilder.setLength(stringBuilder.length()-1);
    	stringBuilder.append("}");
    	return stringBuilder.toString();
    }
    
}

/**
 * Vertex represents a vertex in graph with edge linking to or from
 * Mutable.
 * This class is internal to the rep of ConcreteVerticesGraph.
 * 
 * <p>PS2 instructions: the specification and implementation of this class is
 * up to you.
 * @param <L>
 */
class Vertex<L> {
    
    private L label;
    private final Map<L, Integer> targets;
    private final Map<L, Integer> sources;
    
    /**
     * Abstraction function:
     * 	represents a L label vertex with weighted edges linking to targets 
     * 	and linking from sources
     * 
     * Representation invariant:
     * 	label not in targets and sources
     * 	weight > 0
     * 
     * Safety from rep exposure:
     * 	all fields are private and final, label type {@code L} is immutable
     * 	targets and sources return with defensive deep copy
     * 
     */
    
    /**
     * Vertex Constructor
     * @param label label of vertex
     */
    public Vertex(L label) {
		this.label = label;
		this.targets = new HashMap<>();
		this.sources = new HashMap<>();
		checkRep();
	}
    
    private void checkRep() {
    	assert !targets.containsKey(label);
    	assert !sources.containsKey(label);
    	for(Integer weight : targets.values())
    		assert weight > 0;
    	for(Integer weight : sources.values())
    		assert weight > 0;
    }
    
    /**
     * get label
     * @return vertex label
     */
    public L getLable() {
    	return this.label;
    }
    
    /**
     * check if vertex link to target
     * @param target target vertex label
     * @return true if vertex link to target; else false
     */
    public boolean hasTarget(L target) {
    	return targets.containsKey(target);
    }
    
    /**
     * check if vertex link from source
     * @param source source vertex label
     * @return true if vertex link from source; else false
     */
    public boolean hasSource(L source) {
    	return sources.containsKey(source);
    }
    
    /**
     * get targets linking from this vertex
     * @return targets of vertex
     */
    public Map<L, Integer> targets() {
    	Map<L, Integer> res = new HashMap<>(this.targets);
    	checkRep();
    	return res;
    }
    
    /**
     * get sources linking to this vertex
     * @return sources of vertex
     */
    public Map<L, Integer> sources() {
    	Map<L, Integer> res = new HashMap<>(this.sources);
    	checkRep();
    	return res;
    }
    
    /**
     * get weight of edge linking from this vertex to target
     * @param target target vertex
     * @return return 0 if edge to target not exist; 
     * else return edge weight from this vertex to target  
     */
    public int getTargetWeight(L target) {
    	if(!hasTarget(target))
    		return 0;
    	else
    		return targets.get(target);
    }
    
    /**
     * get weight of edge linking from source to this vertex
     * @param source source vertex
     * @return return 0 if edge from source not exist; 
     * else return edge weight from source to this vertex
     */
    public int getSourceWeight(L source) {
    	if(!hasSource(source))
    		return 0;
    	else
    		return sources.get(source);
    }
    
    /**
     * Add, change, or remove a weighted directed edge linking from this vertex to target.
     * If weight is nonzero, add an edge or update the weight of that edge; target with 
     * the given label is linked from the vertex if it hasn't been linked to. If weight is 
     * zero, remove the edge if it exists (the vertex edge is not otherwise modified).
     * @param target label of target vertex
     * @param weight edge weight
     * @return the previous weight of the edge, or zero if there was no such edge
     */
    public int setTarget(L target, int weight) {
    	Integer oldWeight = targets.put(target, weight);
    	if(weight == 0)
    		targets.remove(target);
    	if(null != oldWeight)
    		return oldWeight;
    	else {
    		return 0;
    	}
    }
    
    /**
     * Add, change, or remove a weighted directed edge linking to this vertex from source.
     * If weight is nonzero, add an edge or update the weight of that edge; source vertex with 
     * the given label is linked to the vertex if it hasn't been linked from. If weight is 
     * zero, remove the edge if it exists (the vertex edge is not otherwise modified).
     * @param source label of source vertex
     * @param weight edge weight
     * @return the previous weight of the edge, or zero if there was no such edge
     */
    public int setSource(L source, int weight) {
    	Integer oldWeight = sources.put(source, weight);
    	if(weight == 0)
    		sources.remove(source);
    	if(null != oldWeight)
    		return oldWeight;
    	else {
    		return 0;
    	}
    }
    
    @Override public String toString() {
    	StringBuilder stringBuilder = new StringBuilder();
    	stringBuilder.append(label);
    	// append targets
    	for(Entry<L, Integer> entry : targets.entrySet()) {
    		stringBuilder.append("(->")
    			.append(entry.getKey())
    			.append(":").append(entry.getValue())
    			.append(")");
    	}
    	// append sources
    	for(Entry<L, Integer> entry : sources.entrySet()) {
    		stringBuilder.append("(<-")
    			.append(entry.getKey())
    			.append(":").append(entry.getValue())
    			.append(")");
    	}
    	checkRep();
    	return stringBuilder.toString();
    }
    
}
