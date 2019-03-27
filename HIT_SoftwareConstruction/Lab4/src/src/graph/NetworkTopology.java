package graph;

import java.util.Arrays;
import java.util.List;

import edge.Edge;
import edge.NetworkConnection;
import vertex.IpVertex;
import vertex.Vertex;

/**
 * Represents Network Topology. it is simple Graph, weighted Graph and has
 * multiple Vertex types. The Vertex type of NetworkTopology should be either
 * {@link Computer}, {@link Server} and {@link Router}, the edge Type is
 * {@link NetworkConnection}. The connection can't be build between a vertex and
 * itself, between Server and Server or between Computer and Computer.
 */
public class NetworkTopology extends ConcreteGraph {

	/**
	 * AF : use a HashSet of Vertex to represent vertices of graph; use a Hashset of
	 * Edge to represent edges of graph
	 * 
	 * RI : vertices is a set with no same vertices; edges is a set with two
	 * different vertices they can't be Computer in the same time or be Server in
	 * the same time. Every vertex in edge is in vertices.
	 * 
	 * safety from rep exposure: the Collection fields are protected and final, and
	 * can't be accessed from irrelevant classes, because of defensive copy.
	 */
	
	public static List<Class<? extends Vertex>> vertexWhiteList = Arrays.asList(IpVertex.class);
	public static List<Class<? extends Edge>> edgeWhiteList = Arrays.asList(NetworkConnection.class);

	/**
	 * Constructor
	 * 
	 * @param name
	 *            graph name
	 */
	public NetworkTopology(String name) {
		super(name);
		this.directed = false;
		checkRep();
	}

}
