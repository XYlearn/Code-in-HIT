/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package poet;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;


/**
 * Tests for GraphPoet.
 */
public class GraphPoetTest {
    
    /**
     * Testing Strategies
     * 
     * For {@link GraphPoet#poem(String)}
     * covers corpus:
     * 	has no two-edge-long path between given words
     * 	has two-edge-long path between given words
     * 
     * For other static methods
     * covers parameter in different cases
     */
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /**
     * Testing {@link GraphPoet#getWordsLowercase(File)}
     * @throws IOException thrown by consructor
     */
    @Test
    public void testGetWordsLowercase() throws IOException {
    	List<String> wordList = GraphPoet.getWordsLowercase(new File("test/P1/poet/poem1.txt"));
    	assertEquals("expected correct word number", wordList.size(), 8);
    	wordList.forEach(word-> {
    		assertTrue("expected lowercase words", word.toLowerCase().equals(word));
    		});
    	StringBuilder stringBuilder = new StringBuilder();
    	for(String word : wordList) {
    		stringBuilder.append(word);
    		stringBuilder.append(" ");
    	}
    	assertTrue(stringBuilder.toString().equals("php is the best language in the world! "));
    }
    
    /**
     * Testing {@link GraphPoet#getWords(String)}
     */
    @Test 
    public void testGetWords() {
    	String text = "PHp BesT.";
    	List<String> wordList = GraphPoet.getWords(text);
    	assertTrue("expected two words", wordList.size()==2);
    	assertTrue("expected words in original case", wordList.get(0).equals("PHp")
    			&& wordList.get(1).equals("BesT."));
    }
    
    /**
     * Testing {@link GraphPoet#buildPoemFromWords(List)}
     */
    @Test
    public void testBuildPoem() {
    	List<String> wordList = Arrays.asList("I'm", "a", "GOoD", "language.");
    	String poem = GraphPoet.buildPoemFromWords(wordList);
    	assertTrue("expected poem words splited by single space", 
    			poem.trim().equals("I'm a GOoD language."));
    }
    
    /**
     * Testing {@link GraphPoet#poem(String)}
     * covers situations mentioned above
     */
    @Test
    public void testPoemNoRepeatWords() {
    	// covers no bridge
    	File testFile = new File("test/P1/poet/poem1.txt");
    	GraphPoet graphPoet = null;
    	try {
			graphPoet = new GraphPoet(testFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	assertEquals("expected original text", graphPoet.poem("PHP best language"), 
    			"PHP best language");
    	
    	//covers one bridge
    	assertEquals("expected original text", graphPoet.poem("PHP The best language in world!"), 
    			"PHP is The best language in the world!");
    	
    	// covers multiple bridge
    	testFile = new File("test/P1/poet/poem2.txt");
    	try {
			graphPoet = new GraphPoet(testFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	assertEquals("expected text with inserted bridge", graphPoet.poem("Always for me"), 
    			"Always there for me");
    }
    
}
