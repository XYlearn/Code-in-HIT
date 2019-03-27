import graph.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;

/**
 * FriendshipGraph to store relationship graph
 * 
 * @author 
 *
 */

public class FriendshipGraph implements Graph<Person> {

	private final Set<Person> vertices = new HashSet<>();
	private final ArrayList<Friendship> edges = new ArrayList<>();

	/**
	 * Abstraction function: AF(g) = {e1, e2, ..., en | e is edge of graph of type
	 * {@code Friendship}}}
	 * 
	 * Representation invariant: don't exist edges with same source and target
	 * e.weight = 1 for edge in graph for all e in edges, e.source, e.target in
	 * vertices
	 * 
	 * Safety from rep exposure: all fields are private and final return copied
	 * vertices when call vertices() elements of vertices and edges are immutable,
	 * so will not be changed in returned collections
	 */

	public FriendshipGraph() {
		checkRep();
	}

	/**
	 * Check representation
	 * 
	 * @return representation
	 */
	private void checkRep() {
		// traversal edges
		for (Friendship edge : edges) {
			assert edge.getWeight() > 0;
			assert !edge.getSource().equals(edge.getTarget());
			assert vertices.contains(edge.getSource());
			assert vertices.contains(edge.getTarget());
		}
	}

	@Override
	public boolean add(Person vertex) {
		boolean res = vertices.add(vertex);
		checkRep();
		return res;
	}

	@Deprecated
	@Override
	public int set(Person source, Person target, int weight) {
		int oldWeight = 0; // oldWeight for return
		boolean vertexNotExist = false; // mark vertex not exists
		
		// check if source exists
		if (!vertices.contains(source)) {
			this.add(source);
			vertexNotExist = true;
		}
		// check if target exists
		if (!vertices.contains(target)) {
			this.add(target);
			vertexNotExist = true;
		}

		// both source and target exist, need to traversal edges
		if (!vertexNotExist) {
			// search for edge
			for (Iterator<Friendship> ite = edges.iterator(); ite.hasNext();) {
				Friendship edge = ite.next();
				// remove found edge and record its weight
				if (edge.link(source, target)) {
					oldWeight = edge.getWeight();
					ite.remove();
					break;
				}
			}
		}

		// add new edge if weight is positive
		if (weight > 0)
			edges.add(new Friendship(source, target, weight));

		checkRep();
		return oldWeight;
	}

	@Override
	public boolean remove(Person vertex) {
		boolean found = false; // mark if vertex is found

		// traversal edges
		for (Iterator<Friendship> ite = edges.iterator(); ite.hasNext();) {
			Friendship edge = ite.next();
			// remove edge related to vertex
			if (edge.isFrom(vertex) || edge.isTo(vertex)) {
				ite.remove();
			}
		}
		found = vertices.remove(vertex);

		checkRep();
		return found;
	}

	@Override
	public Set<Person> vertices() {
		Set<Person> res = new HashSet<>(vertices);
		checkRep();
		return res;
	}

	@Deprecated
	@Override
	public Map<Person, Integer> sources(Person target) {
		Map<Person, Integer> srcs = new HashMap<>(); // result to return

		// traversal edges
		for (Iterator<Friendship> ite = edges.iterator(); ite.hasNext();) {
			Friendship edge = ite.next();
			// find edges linking to target
			if (edge.isTo(target)) {
				srcs.put(edge.getSource(), edge.getWeight());
			}
		}
		checkRep();
		return srcs;
	}

	@Deprecated
	@Override
	public Map<Person, Integer> targets(Person source) {
		Map<Person, Integer> tgts = new HashMap<>(); // result to return

		// traversal edges
		for (Iterator<Friendship> ite = edges.iterator(); ite.hasNext();) {
			Friendship edge = ite.next();
			// find edges linking from source
			if (edge.isFrom(source)) {
				tgts.put(edge.getTarget(), edge.getWeight());
			}
		}
		checkRep();
		return tgts;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		for (Person vertex : vertices) {
			stringBuilder.append(vertex);
			stringBuilder.append("{");
			Map<Person, Integer> tgts = targets(vertex);
			for (Entry<Person, Integer> entry : tgts.entrySet()) {
				stringBuilder.append(new Friendship(vertex, entry.getKey(), entry.getValue()).toString());
			}
			stringBuilder.append("}; ");
		}
		return stringBuilder.toString();
	}
	
	// Methods
	
	/**
	 * add person to graph
	 * @param person Person to add
	 * @return return false if person already exists in graph, else return true
	 */
	public boolean addVertex(Person person) {
		return this.add(person);
	}
	
	/**
	 * add edge between person and friend
	 * if person or friend not exists in graph, it will be automatically added
	 * @param person the person
	 * @param friend person's friend
	 * @return return false if friend is already person's friend or friend is person itself
	 */
	public boolean addEdge(Person person, Person friend) {
		if(person.equals(friend))
			return false;
		int oldWeight = set(person, friend, 1);
		if(oldWeight != 0)
			return false;
		else
			return true;
	}
	
	/**
	 * get persons whoes friend is person 
	 * @param person person 
	 * @return set of persons whoes friend is person. 
	 * return empty set if no one is.
	 */
	public Set<Person> fsSources(Person person) {
		return sources(person).keySet();
	}
	
	/**
	 * get persons who is person's friend 
	 * @param person person 
	 * @return set of persons who is person's friend. 
	 * return empty set if no one is.
	 */
	public Set<Person> fsTargets(Person person) {
		return targets(person).keySet();
	}
	
	/**
	 * get the shortest distance between person1 and person2
	 * @param person1
	 * @param person2
	 * @return shortest distance between person1, person2; 
	 * 	if there is no path between person1 and person2, return -1
	 */
	public int getDistance(Person person1, Person person2) {
		Map<Person, Set<Person>> friendshipMap = new HashMap<>();
		// get friendship map from edges
		for(Friendship friendship : edges) {
			Person source = friendship.getSource();
			Person target = friendship.getTarget();
			if(!friendshipMap.containsKey(source))
				friendshipMap.put(source, new HashSet<>(Arrays.asList(target)));
			else
				friendshipMap.get(source).add(target);
		}
		Set<Person> visited = new HashSet<>();	// visited array
		
		// bfs
		// bfs initialize
		Queue<Person> queue = new LinkedList<>();
		int currDistance = 0;
		queue.add(person1);
		visited.add(person1);
		// loop until all person are visited
		while(!queue.isEmpty()) {
			int size = queue.size();
			for(int i = 0; i < size; i++) {
				Person currPerson = queue.poll();
				// person found
				if(currPerson.equals(person2))
					return currDistance;
				// person has no edge out
				if(!friendshipMap.containsKey(currPerson))
					continue;
				// enqueue adjacent persons
				for(Person person : friendshipMap.get(currPerson)) {
					if(visited.contains(person))
						continue;
					// enqueue
					visited.add(person);
					queue.add(person);
				}
			}
			currDistance++;
		}
		return -1;
	}
	
	/**
	 * main function
	 * @param args unused
	 */
	public static void main(String[] args) {
		FriendshipGraph graph = new FriendshipGraph(); 
		Person rachel = new Person("Rachel"); 
		Person ross = new Person("Ross");
		Person ben = new Person("Ben");
		Person kramer = new Person("Kramer");
		graph.addVertex(rachel); 
		graph.addVertex(ross); 
		graph.addVertex(ben); 
		graph.addVertex(kramer); 
		graph.addEdge(rachel, ross); 
		graph.addEdge(ross, rachel);
		graph.addEdge(ross, ben);
		graph.addEdge(ben, ross);
		System.out.println(graph.getDistance(rachel, ross)); //should print 1 
		System.out.println(graph.getDistance(rachel, ben)); //should print 2 
		System.out.println(graph.getDistance(rachel, rachel));  //should print 0
		System.out.println(graph.getDistance(rachel, kramer));  //should print -1 
	}

}

/**
 * Edge represent directed edge linking two two vertices with positive weight
 * Immutable. This class is internal to the rep of ConcreteEdgesGraph.
 * 
 * <p>
 * PS2 instructions: the specification and implementation of this class is up to
 * you.
 * 
 * @param <Person>
 */
class Friendship {

	private final int weight;
	private final Person source;
	private final Person target;

	/**
	 * Abstraction functions: represent directed edge linking from source Vertex to
	 * target Vertex with weight
	 * 
	 * Representation invariant: source and target are non-empty string && source !=
	 * target && weight > 0
	 * 
	 * Safety from rep exposure: All fields are private and final, all types in rep
	 * is immutable source and target are Persons, so are guaranteed immutable
	 */

	/**
	 * constructor of Edge
	 * 
	 * @param source
	 *            vertex edge link from
	 * @param target
	 *            vertex edge link to
	 * @param weight
	 *            weight of edge
	 */
	public Friendship(Person source, Person target, int weight) {
		this.source = source;
		this.target = target;
		this.weight = weight;
	}

	/**
	 * get weight of edge
	 * 
	 * @return weight of edge
	 */
	public int getWeight() {
		return this.weight;
	}

	/**
	 * get source of edge
	 * 
	 * @return source of edge
	 */
	public Person getSource() {
		return source;
	}

	/**
	 * get target of edge
	 * 
	 * @return target of edge
	 */
	public Person getTarget() {
		return target;
	}

	/**
	 * check representation
	 * 
	 * @return true if rep
	 */
	protected void checkRep() {
		assert !source.equals(target);
		assert weight > 0;
	}

	// TODO methods
	/**
	 * check if edge link two vertices directed
	 * 
	 * @param source
	 *            source vertex link from
	 * @param target
	 *            target vertex link to
	 * @return true if edge link source to target; else false
	 */
	public boolean link(Person source, Person target) {
		return this.source.equals(source) && this.target.equals(target);
	}

	/**
	 * check if edge link from vertex
	 * 
	 * @param source
	 *            source vertex link from
	 * @return true if edge link from vertex; else false
	 */
	public boolean isFrom(Person source) {
		return this.source.equals(source);
	}

	/**
	 * check if edge link to vertex
	 * 
	 * @param target
	 *            source vertex link to
	 * @return true if edge link to vertex; else false
	 */
	public boolean isTo(Person target) {
		return this.target.equals(target);
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("(").append(source).append("->").append(target).append(":").append(weight).append(")");
		checkRep();
		return stringBuilder.toString();
	}

}
