import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import graph.Graph;

public class RouteGraph implements Graph<StopTime> {

	private final Set<StopTime> vertices = new HashSet<>();
	private final List<TripSegment> edges = new ArrayList<>();

	/**
	 * check if two stop in the same place
	 * 
	 * @param stop1
	 *            stop1
	 * @param stop2
	 *            stop2
	 * @return true if stop1 and stop2 have same location and name; else return
	 *         false;
	 */
	private static boolean stopEquals(Stop stop1, Stop stop2) {
		return stop1.getName().equals(stop2.getName()) && stop1.getLatitude() == stop2.getLatitude()
				&& stop1.getLongitude() == stop2.getLongitude();
	}

	@Override
	public boolean add(StopTime vertex) {
		return vertices.add(vertex);
	}

	/**
	 * Add, change, or remove a weighted directed edge in this graph. If weight is
	 * nonzero, add an edge or update the weight of that edge to weight(if source
	 * and target are of different stops) or time difference between target and
	 * source(if source and target are of the same stop); vertices with the given
	 * labels are added to the graph if they do not already exist. If weight is
	 * zero, remove the edge if it exists (the graph is not otherwise modified).
	 * 
	 * @param source
	 *            label of the source vertex
	 * @param target
	 *            label of the target vertex
	 * @param weight
	 *            0 to remove edge, else to update the weight to stop time difference
	 *            the weight is useless in that case.
	 * @return the previous weight of the edge, or zero if there was no such edge
	 */
	@Override
	public int set(StopTime source, StopTime target, int weight) {
		int oldWeight = 0; // oldWeight for return
		boolean vertexNotExist = false; // mark vertex not exists

		// add not existed source
		if (!vertices.contains(source)) {
			this.add(source);
			vertexNotExist = true;
		}
		// add not existed targets
		if (!vertices.contains(target)) {
			this.add(target);
			vertexNotExist = true;
		}

		// both source and target exist, need to traversal edges
		if (!vertexNotExist) {
			// search for edge
			for (Iterator<TripSegment> ite = edges.iterator(); ite.hasNext();) {
				TripSegment edge = ite.next();
				// remove found edge and record its weight
				if (edge.getStartLocation().equals(source) && edge.getEndLocation().equals(target)) {
					oldWeight = edge.getDuration();
					ite.remove();
					break;
				}
			}
		}

		// add new edge if weight is positive
		if (weight > 0) {
			// target stop == source stop, add a weight segment
			if (stopEquals(source, target))
				edges.add(new WaitSegment(source, source.getTime(), target.getTime()));
			// else add a bus segment
			else
				edges.add(new BusSegment(source, target, source.getTime(), target.getTime()));
		}

		return oldWeight;
	}

	@Override
	public boolean remove(StopTime vertex) {
		boolean found = false; // mark if vertex is found

		found = vertices.remove(vertex);
		if (!found)
			return false;

		// traversal edges
		for (Iterator<TripSegment> ite = edges.iterator(); ite.hasNext();) {
			TripSegment edge = ite.next();
			// remove edge related to vertex
			if (stopEquals(edge.getStartLocation(), vertex) || stopEquals(edge.getEndLocation(), vertex)) {
				ite.remove();
			}
		}
		return true;
	}

	@Override
	public Set<StopTime> vertices() {
		return new HashSet<>(vertices);
	}

	@Override
	public Map<StopTime, Integer> sources(StopTime target) {
		Map<StopTime, Integer> srcs = new HashMap<>(); // result to return

		// traversal edges
		for (Iterator<TripSegment> ite = edges.iterator(); ite.hasNext();) {
			TripSegment edge = ite.next();
			// find edges linking to target
			if (stopEquals(edge.getEndLocation(), target)) {
				srcs.put(new StopTime(edge.getStartLocation(), edge.getStartTime()), edge.getDuration());
			}
		}
		return srcs;
	}

	@Override
	public Map<StopTime, Integer> targets(StopTime source) {
		Map<StopTime, Integer> srcs = new HashMap<>(); // result to return

		// traversal edges
		for (Iterator<TripSegment> ite = edges.iterator(); ite.hasNext();) {
			TripSegment edge = ite.next();
			// find edges linking to target
			if (stopEquals(edge.getStartLocation(), source) && 
					edge.getStartTime()==source.getTime()) {
				srcs.put(new StopTime(edge.getEndLocation(), edge.getEndTime()), edge.getDuration());
			}
		}
		return srcs;
	}
	
	/**
	 * check vertex's existence
	 * @param stopTime vertex to check
	 * @return true if exists
	 */
	public boolean containVertex(StopTime stopTime) {
		return vertices.contains(stopTime);
	}

	/**
	 * get size of vertices
	 * 
	 * @return number of vertices
	 */
	public int vSize() {
		return vertices.size();
	}

	/**
	 * get size of edges
	 * 
	 * @return number of edges
	 */
	public int eSize() {
		return edges.size();
	}

	/**
	 * get a copy of graph
	 * 
	 * @return new graph copied from this graph
	 */
	public RouteGraph clone() {
		RouteGraph routeGraph = new RouteGraph();
		routeGraph.vertices.addAll(this.vertices);
		routeGraph.edges.addAll(this.edges);
		return routeGraph;
	}
}
