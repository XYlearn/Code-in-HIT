/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package poet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import graph.Graph;

/**
 * A graph-based poetry generator.
 * 
 * <p>GraphPoet is initialized with a corpus of text, which it uses to derive a
 * word affinity graph.
 * Vertices in the graph are words. Words are defined as non-empty
 * case-insensitive strings of non-space non-newline characters. They are
 * delimited in the corpus by spaces, newlines, or the ends of the file.
 * Edges in the graph count adjacencies: the number of times "w1" is followed by
 * "w2" in the corpus is the weight of the edge from w1 to w2.
 * 
 * <p>For example, given this corpus:
 * <pre>    Hello, HELLO, hello, goodbye!    </pre>
 * <p>the graph would contain two edges:
 * <ul><li> ("hello,") -> ("hello,")   with weight 2
 *     <li> ("hello,") -> ("goodbye!") with weight 1 </ul>
 * <p>where the vertices represent case-insensitive {@code "hello,"} and
 * {@code "goodbye!"}.
 * 
 * <p>Given an input string, GraphPoet generates a poem by attempting to
 * insert a bridge word between every adjacent pair of words in the input.
 * The bridge word between input words "w1" and "w2" will be some "b" such that
 * w1 -> b -> w2 is a two-edge-long path with maximum-weight weight among all
 * the two-edge-long paths from w1 to w2 in the affinity graph.
 * If there are no such paths, no bridge word is inserted.
 * In the output poem, input words retain their original case, while bridge
 * words are lower case. The whitespace between every word in the poem is a
 * single space.
 * 
 * <p>For example, given this corpus:
 * <pre>    This is a test of the Mugar Omni Theater sound system.    </pre>
 * <p>on this input:
 * <pre>    Test the system.    </pre>
 * <p>the output poem would be:
 * <pre>    Test of the system.    </pre>
 * 
 * <p>PS2 instructions: this is a required ADT class, and you MUST NOT weaken
 * the required specifications. However, you MAY strengthen the specifications
 * and you MAY add additional methods.
 * You MUST use Graph in your rep, but otherwise the implementation of this
 * class is up to you.
 */
public class GraphPoet {
    
    private final Graph<String> graph = Graph.empty();
    private final List<String> wordList;
    
    /**
     * Abstraction function:
     * 	represents a word affinity graph which is defined in class document; 
     * 	each vertex in graph represents a nonempty lowercase word in corpus
	 *
     * Representation invariant:
     * 	vertex label has no space and newline-character
     * 	no outier if there are at least 2 words in graph
     * 	in-degree weight sum of vertex = vertex label appearance times - n
     * 		(n = vertex label in head ? 1 : 0) 
     * 	out-degree weigth sum of vertex = vertex label appearance times - n
     * 		(n = vertex label in tail ? 1 : 0)
     * 
     * Safety from rep exposure:
     * 	field graph is private and final and has no outer access 
     * 
     */
    
    /**
     * Create a new poet with the graph from corpus (as described above).
     * 
     * @param corpus text file from which to derive the poet's affinity graph
     * @throws IOException if the corpus file cannot be found or read
     */
    public GraphPoet(File corpus) throws IOException {
    	// read words from file
        this.wordList = getWordsLowercase(corpus);
        
        // do nothing if no words in the file
        if(wordList.isEmpty()) {
        	checkRep();
        	return;
        }
        
        Iterator<String> ite = wordList.iterator();
        // get and word first word
        String prevWord = ite.next();
        graph.add(prevWord);
        
        while(ite.hasNext()) {
        	String word = ite.next();
        	graph.add(word);
        	// update weight
        	int weight = graph.set(prevWord, word, 0);
        	graph.set(prevWord, word, weight + 1);
        	// update prevWord
        	prevWord = word;
        }
        checkRep();
    }

    /**
     * Generate a poem.
     * The concrete is defined above {@link GraphPoet}
     * 
     * @param input string from which to create the poem
     * @return poem (as described above)
     */
    public String poem(String input) {
    	// get original input words
    	List<String> resWords = new LinkedList<>(getWords(input));
    	
    	int size = resWords.size();
    	int idx = 0;
    	if(size == 0)
    		return input;
    	// get the first word
    	String prevWord = resWords.get(idx++);
    	while(idx < size) {
    		String nextWord = resWords.get(idx);
    		// try to get bridge
    		String bridge = getBridge(prevWord, nextWord);
    		
    		// bridge found
    		if(null != bridge) {
    			resWords.add(idx++, bridge.toLowerCase());
    			prevWord = resWords.get(idx);
    			size++;
    		}
    		prevWord = nextWord;
    		idx++;
    	}
    	
    	// use buildPoem to generate result
    	checkRep();
    	return buildPoemFromWords(resWords);
    }
    
    @Override
	public String toString() {
    	return graph.toString();
    }
    
    protected static String buildPoemFromWords(List<String> wordList) {
		StringBuilder stringBuilder = new StringBuilder();
		for(String word : wordList) {
			stringBuilder.append(word);
			// deli with space
			stringBuilder.append(" ");
		}
		stringBuilder.setLength(stringBuilder.length()-1);
		return stringBuilder.toString();
	}
    
    /**
     * get Bridge(defined in class doc) from prevWord to nextWord
     * @param prevWord previous word
     * @param nextWord next word
     * @return if no bridge between prevWord and nextWord, return null;
     * else bridge between prevWord and nextWord
     */
    protected String getBridge(String prevWord, String nextWord) {
    	int maxWeight = 0;
    	String bridge = null;
    	prevWord = prevWord.toLowerCase();
    	nextWord = nextWord.toLowerCase();
    	// traverse for possible bridge word
    	for(Entry<String, Integer> entry : graph.targets(prevWord).entrySet()) {
    		String word = entry.getKey();
    		Map<String, Integer> targets = graph.targets(word);
    		
    		// bridge found
    		if(targets.containsKey(nextWord)) {
    			int weight = targets.get(nextWord) + entry.getValue();
    			// make comparison
    			if(weight > maxWeight) {
    				maxWeight = weight;
    				bridge = word;
    			}
    		}
    	}
    	return bridge;
	}
    
    
    /**
     * get words(defined in class doc) in order from poem file
     * words are stored in lowercase
     * @param file poem file
     * @return list of words in order
     * @throws IOException throws if file cannot be found or read
     */
    protected static List<String> getWordsLowercase(File file) throws IOException {
		List<String> wordList = new ArrayList<>();
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		
		// get words from each line
		for(String line = bufferedReader.readLine(); 
				line != null; line = bufferedReader.readLine()) {
			// append words
			for(String word : line.split(" ")) {
				String trimedWord = word.trim();
				if(!trimedWord.isEmpty())
					wordList.add(trimedWord);
			}
		}
		// to lowercase
		wordList = toLowerCase(wordList);
		
		// close bufferedReader
		bufferedReader.close();
		return wordList;
	}
    
    /**
     * get words(defined in class doc) in order from content
     * @param content content of words in order
     * @return list of words
     */
    protected static List<String> getWords(String content) {
    	List<String> wordList = new ArrayList<>();
    	BufferedReader bufferedReader = new BufferedReader(new StringReader(content));
    	// get words from each line
    	try {
    		for(String line = bufferedReader.readLine(); 
    			line != null; line = bufferedReader.readLine()) {
    			// append words
    			for(String word : line.split(" ")) {
    				String trimedWord = word.trim();
    				if(!trimedWord.isEmpty())
    					wordList.add(trimedWord);
    			}
    		}
    	} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	// close bufferedReader
    	try {
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return wordList;
	}
    
    
    /**
     * create new list contains lowercase words in given list
     * @param wordList original wordList
     * @return lowercase word list
     */
    private static List<String> toLowerCase(List<String> wordList) {
    	List<String> res = new ArrayList<>();
    	for(String word: wordList)
    		res.add(word.toLowerCase());
    	return res;
    }
    
    /**
     * check Representation Invariants
     */
    protected void checkRep() {
    	Set<String> vertices = graph.vertices();
    	vertices.forEach(word -> {
    		assert !word.contains(" ") && !word.contains("\n");
    		assert !word.isEmpty();
    		assert word.toLowerCase().equals(word);
    		});
    	
    	int inDegreeWeight = 0;
    	int outDegreeWeight = 0;
    	Map<String, Integer> inDegreeMap = new HashMap<>();	// save vertices and their in-degrees
    	Map<String, Integer> outDegreeMap = new HashMap<>(); // save vertices and their out-degrees
    	// get in-degree and out-degree
    	for(String vertex : vertices) {
    		// sum up inDegreeWeight
    		for(Integer weight : graph.sources(vertex).values()) 
    			inDegreeWeight += weight;
    		inDegreeMap.put(vertex, inDegreeWeight);
    		inDegreeWeight = 0;
    		// sum up outDegreeWeight
    		for(Integer weight : graph.targets(vertex).values())
    			outDegreeWeight += weight; 		
    		outDegreeMap.put(vertex, outDegreeWeight);
    		outDegreeWeight = 0;
    	}
    	
    	// calculate appearance time
    	HashMap<String, Integer> appearanceMap = new HashMap<>();
    	for(String word: wordList) {
	    	if(appearanceMap.containsKey(word)) {
				appearanceMap.put(word, appearanceMap.get(word) + 1);
			} else {
				appearanceMap.put(word, 1);
			}
    	}
    	// check equality
    	for(Entry<String, Integer> entry : appearanceMap.entrySet()) {
    		String word = entry.getKey();
    		Integer appearance = entry.getValue();
    		assert inDegreeMap.containsKey(word);
    		assert outDegreeMap.containsKey(word);
    		//in-degree weight sum of vertex = vertex label appearance times - n
    		// (n = vertex label in head ? 1 : 0) 
    		assert inDegreeMap.get(word) == 
    				appearance - (wordList.get(0).equals(word) ? 1 : 0);
    		//out-degree weigth sum of vertex = vertex label appearance times - n
    	    //(n = vertex label in tail ? 1 : 0)
    		assert outDegreeMap.get(word) == 
    				appearance - (wordList.get(wordList.size()-1).equals(word) ? 1 : 0);
    	}
    }
    
    
}
