import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * FriendshipGraph to store relationship graph
 * @author 
 *
 */

public class FriendshipGraph {
	protected HashMap<String, Integer> personIndexes; //store Person index in friendsList and personList 
	protected ArrayList<ArrayList<Integer>> friendsList; // List of each one's friends
	protected ArrayList<Person> personList; // List of Persons
	protected int vertexNum; // the number of vertexes
	
	/**
	 * 
	 * @param person
	 * @return return person index; if person doesn't exist, return -1
	 */
	private int getPersonIndex(Person person) {
		if(!personIndexes.containsKey(person.getName())) {
			return -1;
		} else {
			return personIndexes.get(person.getName());
		}
	}
	
	/**
	 * Initialize
	 */
	public FriendshipGraph() {
		this.personIndexes = new HashMap<String, Integer>();
		this.friendsList = new ArrayList<ArrayList<Integer>>();
		this.personList = new ArrayList<Person>();
		vertexNum = 0;
	}
	
	/**
	 * 
	 * @param person person Object
	 * @param friend person's friend
	 * @return check result
	 */
	public boolean isFriend(Person person, Person friend) {
		int personIndex = getPersonIndex(person);
		int friendIndex = getPersonIndex(friend);
		if(personIndex == -1 || friendIndex == -1)
			return false;
		for(Integer index : friendsList.get(personIndex))
			if(index == friendIndex)
				return true;
		return false;
	}
	
	/**
	 * 
	 * @param person Person to add
	 * @return return -1 if person already exists in graph, else return person index
	 */
	public int addVertex(Person person) {
		if(personIndexes.containsKey(person.getName())) {
			return -1;
		}
		personList.add(person);
		personIndexes.put(person.getName(), vertexNum++);
		friendsList.add(new ArrayList<Integer>());
		return vertexNum-1;
	}
	
	/**
	 * add edge between person and friend
	 * if person or friend not exists in graph, it will be automatically added
	 * @param person the person
	 * @param friend person's friend
	 * @return return false if friend is already person's friend or friend is person itself
	 */
	public boolean addEdge(Person person, Person friend) {
		// get index
		int personIndex, friendIndex;
		personIndex = getPersonIndex(person);
		friendIndex = getPersonIndex(friend);
		
		// automatically addVertex if person or friend not exists in list
		if(personIndex == -1) 
			personIndex = addVertex(person);
		if(friendIndex == -1)
			friendIndex = addVertex(friend);
		
		// check
		if(personIndex == friendIndex)
			return false;
		if(isFriend(person, friend))
			return false;
		
		// add edge
		friendsList.get(personIndex).add(friendIndex);
		return true;
	}
	
	/**
	 * 
	 * @param person1
	 * @param person2
	 * @return shortest distance between person1, person2; 
	 * 	if there is no path between person1 and person2, return -1
	 */
	public int getDistance(Person person1, Person person2) {
		int index1 = getPersonIndex(person1);
		int index2 = getPersonIndex(person2);
		if(index1 == index2)
			return 0;
		// initialize visited array
		ArrayList<Boolean> visited = new ArrayList<Boolean>();
		for(int i = 0; i < vertexNum; i++)
			visited.add(false);
		
		// bfs
		int distance = 0;
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.offer(index1);
		visited.set(index1, true);
		while(!queue.isEmpty()) {
			int queue_size = queue.size();
			distance += 1; // increase distance
			for(int i = 0; i < queue_size; i++) {
				index1 = queue.poll();
				for(Integer index : friendsList.get(index1)) {
					if(visited.get(index) == true)
						continue;
					if(index == index2)
						return distance;
					queue.offer(index);
					
					// mark as visited
					visited.set(index, true);
				}
			}
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
