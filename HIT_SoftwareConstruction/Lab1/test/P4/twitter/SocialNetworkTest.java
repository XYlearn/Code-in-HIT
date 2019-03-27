/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SocialNetworkTest {
	private static final Instant d0 = Instant.ofEpochSecond(0);
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-11-23T10:00:00Z");
    private static final Instant d4 = Instant.parse("2017-02-18T10:59:59Z");
	
	private static final Tweet tweet1 = new Tweet(1, "abc", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "def", "@rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "Ghi", "I'm @rivest @jKL", d3);
    private static final Tweet tweet4 = new Tweet(4, "jkL", "@mno \nhas\t words to say @jkL", d3);
    private static final Tweet tweet5 = new Tweet(5, "mno", "@stu.hit.edu.cn is @abc 's email name @jkl", d4);

    /**
     * Testing Strategies
     * 
     * 
     * covers list size : 0, 1, >2  
     * 		  situation : user mentioned is in author list or not
     * 					  have User names in different cases
     * 			
     */
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /**
     * Testing guessFollowsGraph
     * covers list size = 0
     */
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }
    
    /**
     * Testing guessFollowsGraph
     * covers 
     * 		list size = 1, 2
     * 		user mentioned not in author list
     * 		username in different case
     */
    public void testGuessFollowsGraphNotEmpty() {
    	Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet2));
    	assertEquals("expected one element in graph", 1, followsGraph.values().size());
    	
    	followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5));
    	assertEquals("expected 4 elements in graph", 4, followsGraph.size());
    	for(Map.Entry<String, Set<String>> entry : followsGraph.entrySet()) {
    		if(entry.getKey().toLowerCase().equals(tweet1.getAuthor().toLowerCase()))
    			assertEquals("expected correct follower set", 0, entry.getValue().size());
    		else if(entry.getKey().toLowerCase().equals(tweet2.getAuthor().toLowerCase()))
    			assertEquals("expected correct follower set", 1, entry.getValue().size());
    		else if(entry.getKey().toLowerCase().equals(tweet3.getAuthor().toLowerCase()))
    			assertEquals("expected correct follower set", 2, entry.getValue().size());
    		else if(entry.getKey().toLowerCase().equals(tweet4.getAuthor().toLowerCase()))
    			assertEquals("expected correct follower set", 2, entry.getValue().size());
    		else if(entry.getKey().toLowerCase().equals(tweet5.getAuthor().toLowerCase()))
    			assertEquals("expected correct follower set", 1, entry.getValue().size());
    	}
    }
    
    /**
     * Testing influencers
     * covers list size = 0
     */
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }

    /**
     * Testing influencers
     * covers list size != 0
     * 		  username in different case
     */
    @Test
    public void testInfluencersNotEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put(tweet3.getAuthor(), new HashSet<String>(Arrays.asList("rivest", "jKL")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected non-empty list", influencers.size() == 2);
        
        followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5));
        influencers = SocialNetwork.influencers(followsGraph);
        assertEquals("expected e elements in list", 4, influencers.size());
        assertEquals("expected correct order", influencers.get(0).toLowerCase(), "jkl");
        assertEquals("expected correct order", influencers.get(1).toLowerCase(), "rivest");
    }
    
    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */

}
