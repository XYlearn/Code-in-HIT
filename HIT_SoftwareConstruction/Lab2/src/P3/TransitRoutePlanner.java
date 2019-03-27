import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class TransitRoutePlanner implements RoutePlanner {

	private final RouteGraph graph;

	
	/**
	 * constructor
	 * 
	 * @param graph
	 *            route graph
	 */
	TransitRoutePlanner(RouteGraph graph) {
		this.graph = graph;
	}

	@Override
	public List<Stop> findStopsBySubstring(String search) {
		Set<Stop> res = new HashSet<>();

		for (Stop stop : graph.vertices()) {
			if (stop.getName().contains(search))
				res.add(Stop.getInstance(stop));
		}

		return new ArrayList<>(res);
	}

	@Override
	public Itinerary computeRoute(Stop src, Stop dst, int time) {
		ItineraryBuilder builder = new ItineraryBuilder("");

		// clone a graph for computing
		RouteGraph routeGraph = getComputeGraph(src, time);
		final StopTime srcStopTime = new StopTime(src, time);

		/**
		 * represents Node of BFS
		 * to find path
		 */
		class BFSNode {
			public StopTime stopTime;
			public BFSNode lastNode;
			
			public BFSNode(StopTime stopTime, BFSNode lastNode) {
				this.stopTime = stopTime;
				this.lastNode = lastNode;
			}
		}
		
		// initialize bfs
		HashSet<StopTime> visited = new HashSet<>();
		Queue<BFSNode> queue = new LinkedList<>();
		visited.add(srcStopTime);
		queue.add(new BFSNode(srcStopTime, null));

		Set<BFSNode> accessibleNodes = new HashSet<>();	// accessible BFSNode wrapping destination stop
		
		// bfs for accessible StopTime
		while (!queue.isEmpty()) {
			BFSNode node = queue.remove();
			StopTime stopTime = node.stopTime;
			for(StopTime currStopTime : routeGraph.targets(stopTime).keySet()) {
				// add found node to accessibleNodes
				if(currStopTime.stopEquals(dst)) {
					accessibleNodes.add(new BFSNode(currStopTime, node));
					continue;
				}
				if(!visited.contains(currStopTime)) {
					queue.add(new BFSNode(currStopTime, node));
					visited.add(currStopTime);
				}
			}
		}
		
		// select the earliest accessible stoptime
		int minTime = Integer.MAX_VALUE;
		BFSNode selectedNode = null;
		for(BFSNode node : accessibleNodes) {
			if(node.stopTime.getTime() < minTime) {
				minTime = node.stopTime.getTime();
				selectedNode = node;
			}
		}
		
		if (null == selectedNode)
			return builder.build();
		
		// build Itinerary
		BFSNode nextNode = selectedNode;
		BFSNode lastNode = nextNode.lastNode;
		while(null != lastNode) {
			builder.add(lastNode.stopTime, nextNode.stopTime);
			nextNode = lastNode;
			lastNode = lastNode.lastNode;
		}
		
		return builder.build();
	}

	/**
	 * get RouteGraph for route computing
	 * 
	 * @param src
	 *            departure place
	 * @param time
	 *            time to start, time >= 0
	 * @return generate graph for route computing
	 */
	private RouteGraph getComputeGraph(Stop src, int time) {
		// clone a graph
		RouteGraph routeGraph = graph.clone();
		
		// add srcStopTime
		StopTime srcStopTime = new StopTime(src, time);
		
		if(graph.containVertex(srcStopTime))
			return routeGraph;
		
		routeGraph.add(srcStopTime);
		
		// set edges between srcStopTime and others in same place
		for (StopTime stopTime : routeGraph.vertices()) {
			if (stopTime.stopEquals(srcStopTime)) {
				int timeDiff = stopTime.getTime() - srcStopTime.getTime();
				if (timeDiff > 0 && timeDiff <= 1200)
					routeGraph.set(srcStopTime, stopTime, timeDiff);
			}
		}
		return routeGraph;
	}
}
