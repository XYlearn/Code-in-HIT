package graph;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edge.Edge;
import edge.WordNeighborhood;
import util.ParseUtil;
import vertex.Vertex;
import vertex.Word;

/**
 * A graph-based poetry generator.
 * 
 * <p>
 * GraphPoet is initialized with a corpus of text, which it uses to derive a
 * word affinity graph. Vertices in the graph are words. Words are defined as
 * non-empty case-insensitive strings of non-space non-newline characters. They
 * are delimited in the corpus by spaces, newlines, or the ends of the file.
 * Edges in the graph count adjacencies: the number of times "w1" is followed by
 * "w2" in the corpus is the weight of the edge from w1 to w2.
 * 
 * <p>
 * For example, given this corpus:
 * 
 * <pre>
 *     Hello, HELLO, hello, goodbye!
 * </pre>
 * <p>
 * the graph would contain two edges:
 * <ul>
 * <li>("hello,") -> ("hello,") with weight 2
 * <li>("hello,") -> ("goodbye!") with weight 1
 * </ul>
 * <p>
 * where the vertices represent case-insensitive {@code "hello,"} and
 * {@code "goodbye!"}.
 * 
 * <p>
 * Given an input string, GraphPoet generates a poem by attempting to insert a
 * bridge word between every adjacent pair of words in the input. The bridge
 * word between input words "w1" and "w2" will be some "b" such that w1 -> b ->
 * w2 is a two-edge-long path with maximum-weight weight among all the
 * two-edge-long paths from w1 to w2 in the affinity graph. If there are no such
 * paths, no bridge word is inserted. In the output poem, input words retain
 * their original case, while bridge words are lower case. The whitespace
 * between every word in the poem is a single space.
 * 
 * <p>
 * For example, given this corpus:
 * 
 * <pre>
 *     This is a test of the Mugar Omni Theater sound system.
 * </pre>
 * <p>
 * on this input:
 * 
 * <pre>
 *     Test the system.
 * </pre>
 * <p>
 * the output poem would be:
 * 
 * <pre>
 *     Test of the system.
 * </pre>
 * 
 */
public class GraphPoet extends ConcreteGraph {

	/**
	 * Abstraction function: represents a word affinity graph which is defined in
	 * class document; each vertex in graph represents a nonempty lowercase word in
	 * corpus
	 *
	 * Representation invariant: vertex label has no space and newline-character no
	 * outier if there are at least 2 words in graph in-degree weight sum of vertex
	 * = vertex label appearance times - n (n = vertex label in head ? 1 : 0)
	 * out-degree weigth sum of vertex = vertex label appearance times - n (n =
	 * vertex label in tail ? 1 : 0)
	 * 
	 * Safety from rep exposure: field graph is private and final and has no outer
	 * access
	 * 
	 */
	
	public static List<Class<? extends Vertex>> vertexWhiteList = Arrays.asList(Word.class);
	public static List<Class<? extends Edge>> edgeWhiteList = Arrays.asList(WordNeighborhood.class);

	private int weightThreshold;

	/**
	 * Default constructor
	 * 
	 * @param name
	 *            graph name
	 */
	public GraphPoet(String name) {
		super(name);
		this.directed = true;
		// set to infinity by default
		weightThreshold = 0;
		checkRep();
	}

	/**
	 * set threshold of edge weight. Edges that has less than threshold weight will
	 * not appear in the graph
	 * 
	 * @param threshold
	 *            threshold to set
	 */
	public void setWeightThreshold(int threshold) {
		this.weightThreshold = threshold;
	}

	/**
	 * get current weight threshold of edge
	 * 
	 * @return weight threshold of edge
	 */
	public int getWeightThreshold() {
		return weightThreshold;
	}

	/**
	 * Get a {@link Map} where keys represents sources vertices which has a edge to
	 * target and the value represent the weight of the edge. and All edges whose
	 * weight less than weight threshold will be ignored
	 * 
	 * @param target
	 *            the vertex which we are going to find source vertices connect to
	 * 
	 * @return return a map which is defined above
	 */
	@Override
	public Map<Vertex, List<Double>> sources(Vertex target) {
		Map<Vertex, List<Double>> res = new HashMap<>();

		// if vertex is not in vertices, it has no sources
		if (!vertices.contains(target))
			return res;

		for (Edge edge : edges) {
			if (edge.getWeight() < weightThreshold)
				continue;
			if (edge.targetVertices().contains(target)) {
				// get all source vertices
				Set<Vertex> sourceVertices = edge.sourceVertices();

				// add all source vertices to result
				for (Vertex source : sourceVertices) {
					if (res.containsKey(source)) {
						res.get(source).add(edge.getWeight());
					} else {
						res.put(source, Arrays.asList(edge.getWeight()));
					}
				}
			}
		}

		return res;
	}

	/**
	 * Get a {@link Map} where keys represents target vertices which has a edge to
	 * source and the value represent the weight of the edge. and All edges whose
	 * weight less than weight threshold will not be ignored
	 * 
	 * @param source
	 *            the vertex which we are going to find target vertices connect to
	 * @return return a map which is defined above
	 */
	@Override
	public Map<Vertex, List<Double>> targets(Vertex source) {
		Map<Vertex, List<Double>> res = new HashMap<>();

		// if vertex is not in vertices, it has no sources
		if (!vertices.contains(source))
			return res;

		for (Edge edge : edges) {
			if (edge.getWeight() < weightThreshold)
				continue;
			if (edge.sourceVertices().contains(source)) {
				// get all source vertices
				Set<Vertex> targetVertices = edge.targetVertices();

				// add all source vertices to result
				for (Vertex target : targetVertices) {
					if (res.containsKey(source)) {
						res.get(target).add(edge.getWeight());
					} else {
						res.put(target, Arrays.asList(edge.getWeight()));
					}
				}
			}
		}

		return res;
	}

	/**
	 * Get set of edges whose weight >= weight threshold in the graph
	 * 
	 * @return Set of edges in the graph
	 */
	@Override
	public Set<Edge> edges() {
		Set<Edge> res = new HashSet<>();
		for (Edge edge : edges) {
			// defensive copy
			assert edge.valid();
			if (edge.getWeight() >= weightThreshold)
				res.add(edge.clone());
		}
		return res;
	}

	/**
	 * get the number of edges in graph. Only edges with weight >= weight threshold
	 * will be taken in account
	 * 
	 * @return the number of edges in graph
	 */
	@Override
	public int edgeCount() {
		int count = 0;
		for (Edge edge : edges)
			if (edge.getWeight() >= weightThreshold)
				count += 1;
		return count;
	}

	/**
	 * Constructor of GraphPoet, it reads corpus from file and constructs a Graph
	 * for poet. if if file not exists or can't be read, an empty graph will be
	 * constructed.
	 * 
	 * @param name
	 *            graph name
	 * @param pathname
	 *            pathname of corpus file
	 */
	public GraphPoet(String name, String pathname) throws IOException {
		super(name);
		List<String> wordList;
		try {
			wordList = ParseUtil.getWordsFromFile(pathname);
		} catch (IOException e) {
			wordList = Arrays.asList();
			throw e;
		}
		wordList = ParseUtil.wordsToLowercase(wordList);
		if (wordList.isEmpty())
			return;

		Iterator<String> ite = wordList.iterator();
		// get and word first word
		String prevWord = ite.next();
		addVertex(Word.wrap(prevWord));

		while (ite.hasNext()) {
			String word = ite.next();
			addVertex(Word.wrap(word));
			// update weight
			increaseWeight(prevWord, word);
			// update prevWord
			prevWord = word;
		}
	}

	/**
	 * Constructor of GraphPoet. It constructs a GraphPoet from the given words
	 * 
	 * @param name
	 *            name of Graph
	 * @param words
	 *            words to construct from
	 */
	public GraphPoet(String name, String[] words) {
		super(name);
		if (words.length == 0)
			return;
		String prevWord = words[0];
		addVertex(Word.wrap(prevWord));

		for (int i = 1; i < words.length; i++) {
			String word = words[i];
			addVertex(Word.wrap(word));
			// update weight
			increaseWeight(prevWord, word);
			prevWord = word;
		}
		checkRep();
	}

	/**
	 * Generate a poem. The concrete is defined above {@link GraphPoet}
	 * 
	 * @param input
	 *            string from which to create the poem
	 * @return poem (as described above)
	 */
	public String poem(String input) {
		// get original input words
		List<String> resWords = new LinkedList<>(ParseUtil.getWords(input));

		int size = resWords.size();
		int idx = 0;
		if (size == 0)
			return input;
		// get the first word
		String prevWord = resWords.get(idx++);
		while (idx < size) {
			String nextWord = resWords.get(idx);
			// try to get bridge
			String bridge = getBridge(prevWord, nextWord);

			// bridge found
			if (!bridge.isEmpty()) {
				resWords.add(idx++, bridge.toLowerCase());
				prevWord = resWords.get(idx);
				size++;
			}
			prevWord = nextWord;
			idx++;
		}

		return String.join(" ", resWords);
	}

	/**
	 * Increase the weight of edge, if the edge doesn't exists, it will be added to
	 * graph and its weight will be set to 1. If the source or target doesn't exists
	 * in the graph, increase will fail and return false.
	 * 
	 * @param source
	 *            source Word's label
	 * @param target
	 *            target Word's label
	 * @return true if the weight increased or new edge added
	 */
	protected boolean increaseWeight(String source, String target) {
		if (!edges.contains(WordNeighborhood.wrap(source, target, 1))) {
			if (!vertices.contains(Word.wrap(source)) || !vertices.contains(Word.wrap(target)))
				return false;
			edges.add(WordNeighborhood.wrap(source, target, 1));
			return true;
		}
		for (Edge wordEdge : edges) {
			if (wordEdge.equals(WordNeighborhood.wrap(source, target, 1))) {
				wordEdge.setWeight(wordEdge.getWeight() + 1);
				checkRep();
				return true;
			}
		}
		return false;
	}

	/**
	 * get Bridge(defined in class doc) from prevWord to nextWord
	 * 
	 * @param prevWord
	 *            previous word vertex
	 * @param nextWord
	 *            next word vertex
	 * @return if no bridge between prevWord and nextWord, return empty String; else
	 *         bridge between prevWord and nextWord
	 */
	protected String getBridge(String prevWord, String nextWord) {
		double maxWeight = 0;
		String bridge = "";
		prevWord = prevWord.toLowerCase();
		nextWord = nextWord.toLowerCase();
		// traverse for possible bridge word
		for (Entry<Vertex, List<Double>> entry : targets(Word.wrap(prevWord)).entrySet()) {
			Vertex wordVertex = entry.getKey();
			Map<Vertex, List<Double>> targets = targets(wordVertex);
			
			// bridge found
			if (targets.containsKey(Word.wrap(nextWord))) {
				double weight = targets.get(Word.wrap(nextWord)).get(0) + entry.getValue().get(0);
				// make comparison
				if (weight > maxWeight) {
					maxWeight = weight;
					bridge = wordVertex.getLabel();
				}
			}
		}
		return bridge;
	}
	
	@Override
	public boolean allowLoop() {
		return true;
	}

}
