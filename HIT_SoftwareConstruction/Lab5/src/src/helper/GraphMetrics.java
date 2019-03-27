package helper;

import java.util.Set;

import edge.Edge;
import graph.Graph;
import vertex.Vertex;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.scoring.*;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import helper.centrality.*;
import util.GraphConverter;

public class GraphMetrics {
	
	private CentralityStrategy centrality;
	
	/**
	 * Constructor
	 * @param centrality strategy to calculate centrality
	 */
	public GraphMetrics(CentralityStrategy centrality) {
		this.centrality = centrality;
	}
	
	public void setCentrality(CentralityStrategy centrality) {
		this.centrality = centrality;
	}
	
	public double calculateCentrality(Graph<Vertex, Edge> g, Vertex v) {
		return this.centrality.calculate(g, v);
	}
	
	/**
	 * calculate the degree centrality of Vertex v in Graph g
	 * 
	 * @param g
	 *            Graph
	 * @param v
	 *            Vertex
	 * @return the degree centrality of Graph g. if v doesn't exist in g, return -1
	 */
	public static double degreeCentrality(Graph<Vertex, Edge> g, Vertex v) {
		if (!g.containVertex(v))
			return -1;
		return GraphConverter.convert2Jung(g).degree(v);
	}

	/**
	 * calculate the total degree centrality Graph g
	 * 
	 * @param g
	 *            Graph
	 * @return the degree centrality of Graph g
	 */
	public static double degreeCentrality(Graph<Vertex, Edge> g) {
		Vertex[] vertices = new Vertex[g.vertexCount()];
		g.vertices().toArray(vertices);
		double maxDegree = Double.MIN_VALUE;
		double sumDegree = 0;
		for (int i = 0; i < vertices.length; i++) {
			double degree = degreeCentrality(g, vertices[i]);
			sumDegree += degree;
			if (degree > maxDegree)
				maxDegree = degree;
		}
		double centrality = ((maxDegree * g.vertexCount()) - sumDegree) / (g.vertexCount() * (g.vertexCount() - 3) + 2);

		return centrality;
	}

	/**
	 * calculate the closeness centrality of Vertex v in Graph g
	 * 
	 * @param g
	 *            Graph
	 * @param v
	 *            Vertex
	 * @return the closeness centrality of Graph g. if v doesn't exist in g, return
	 *         -1
	 */
	public static double closenessCentrality(Graph<Vertex, Edge> g, Vertex v) {
		if (!g.containVertex(v))
			return -1;
		ClosenessCentrality<Vertex, Edge> centrality = new ClosenessCentrality<>(GraphConverter.convert2Jung(g));
		return centrality.getVertexScore(v);
	}

	/**
	 * calculate the betweenness centrality of Vertex v in Graph g
	 * 
	 * @param g
	 *            Graph
	 * @param v
	 *            Vertex
	 * @return the betweenness centrality of Graph g. if v doesn't exist in g,
	 *         return -1
	 */
	public static double betweennessCentrality(Graph<Vertex, Edge> g, Vertex v) {
		if (!g.vertices().contains(v))
			return -1;
		BetweennessCentrality<Vertex, Edge> centrality = new BetweennessCentrality<>(GraphConverter.convert2Jung(g));
		centrality.setRemoveRankScoresOnFinalize(false);
		centrality.evaluate();
		return centrality.getVertexRankScore(v);
	}

	/**
	 * calculate the inDegree centrality of Vertex v in weighted Graph g
	 * 
	 * @param g
	 *            Graph
	 * @param v
	 *            Vertex
	 * @return the inDegree centrality of Graph g. if v doesn't exist in g, return
	 *         -1
	 */
	public static double inDegreeCentrality(Graph<Vertex, Edge> g, Vertex v) {
		if (!g.containVertex(v))
			return -1;
		return GraphConverter.convert2Jung(g).inDegree(v);
	}

	/**
	 * calculate the outDegree centrality of Vertex v in weighted Graph g
	 * 
	 * @param g
	 *            Graph
	 * @param v
	 *            Vertex
	 * @return the outDegree centrality of Graph g. if v doesn't exist in g, return
	 *         -1
	 */
	public static double outDegreeCentrality(Graph<Vertex, Edge> g, Vertex v) {
		if (!g.containVertex(v))
			return -1;
		return GraphConverter.convert2Jung(g).outDegree(v);
	}

	/**
	 * calculate the shortest distance between start and end in Graph g
	 * 
	 * @param g
	 *            Graph
	 * @param start
	 *            startVertex
	 * @param end
	 *            endVertex
	 * @return the shortest distance between start and end. if start or end doesn't
	 *         exist in g, return -1. if the distance is infinity, return -2.
	 */
	public static double distance(Graph<Vertex, Edge> g, Vertex start, Vertex end) {
		if (!g.containVertex(start) || !g.containVertex(end))
			return -1;
		edu.uci.ics.jung.graph.Graph<Vertex, Edge> graph = GraphConverter.convert2Jung(g);
		Number dis = new DijkstraDistance<>(graph).getDistance(start, end);
		if (null != dis)
			return dis.doubleValue();
		else
			return -2;
	}

	/**
	 * calculate the eccentricity of Vertex v in Graph g
	 * 
	 * @param g
	 *            Graph
	 * @param v
	 *            Vertex
	 * @return eccentricity of Vertex v in Graph g. if v isn't connected with any
	 *         other vertices, 0 will be returned. if v doesn't exist in g, return
	 *         -1. if the eccentricity is infinity, return -2
	 */
	public static double eccentricity(Graph<Vertex, Edge> g, Vertex v) {
		DijkstraDistance<Vertex, Edge> dijkstraDistance = new DijkstraDistance<>(GraphConverter.convert2Jung(g));
		Set<Vertex> targets = g.vertices();
		targets.remove(v);
		
		if(!g.containVertex(v))
			return -1;

		double ecc = 0;
		for (Vertex target : targets) {
			Number dis = dijkstraDistance.getDistance(v, target);
			if(null == dis)
				return -2;
			if (dis.doubleValue() > ecc)
				ecc = dis.doubleValue();
		}
		return ecc;
	}

	/**
	 * return the radius of Graph g.the radius of a graph is the minimum
	 * eccentricity of any vertex
	 * 
	 * @param g
	 *            Graph
	 * @return the radius of Graph g. if the g has no vertices, -1 will be returned
	 */
	public static double radius(Graph<Vertex, Edge> g) {
		Set<Vertex> vertices = g.vertices();
		if (vertices.isEmpty())
			return -1;
		double r = Double.MAX_VALUE;
		for (Vertex vertex : vertices) {
			r = Double.min(r, eccentricity(g, vertex));
		}
		return r;
	}

	/**
	 * return the diameter of Graph g.the diameter of a graph is the maximum
	 * eccentricity of any vertex
	 * 
	 * @param g
	 *            Graph
	 * @return the diameter of Graph g. if the g has no vertices, -1 will be
	 *         returned
	 */
	public static double diameter(Graph<Vertex, Edge> g) {
		Set<Vertex> vertices = g.vertices();
		if (vertices.isEmpty())
			return -1;
		double d = 0;
		for (Vertex vertex : vertices) {
			d = Double.max(d, eccentricity(g, vertex));
		}
		return d;
	}
}
