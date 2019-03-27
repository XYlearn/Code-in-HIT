/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * writtenBy Test Strategies
     * Input covers:
     * 		tweets number : multiple, single, empty tweets List(not nessesary)
     * 		result number : multiple results, single result, no result
     * 		results order
     * 		origin list not be changed
     * 
     * (writtenBy specific)
     * 		author name   : characters in same case, different case, part
     * 
     * (inTimespan specific)
     * 		tweets.timestamp : in (timespan.start, timespan.end), =timespan.start,
	 * 							 =timespan.end, not in timespan
	 * 
	 * (containing specific)
	 * 		begin with word, end with word, word in middle
	 * 		word is divided with space or not
	 * 		words in different case
	 * 		wordsList is empty, contains one word, contains more than one word
	 * 		words divided with different space characters
     */
    
	private static final Instant d0 = Instant.ofEpochSecond(0);
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-11-23T10:00:00Z");
    private static final Instant d4 = Instant.parse("2017-02-18T10:59:59Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "alyssa", "I'm coser", d3);
    private static final Tweet tweet4 = new Tweet(4, "alyssa", "@DOG\nhas\t words to say @dog", d3);
    private static final Tweet tweet5 = new Tweet(5, "BBitdiDdle", "@stu.hit.edu.cn is @coser 's email name", d4);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /**
     * Testing writtenBy
     * covers : empty tweets
     */
    @Test
    public void testWrittenEmptyTweets() {
    	List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(), "a");
    	
    	assertTrue("expected empty list", writtenBy.isEmpty());
    }
    
    /**
     * Testing writtenBy
     * covers : multiple tweets
     * 			single result
     */
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }

    /**
     * Testing writtenBy
     * covers : multiple tweets
     * 			multiple results 
     * 			results order test
     * 			whether original list be modified 
     */
    @Test
    public void testWrittenByMultipleTweetsMultipleResult() {
    	List<Tweet> originList = Arrays.asList(tweet3, tweet2, tweet1, tweet4, tweet5);
    	List<Tweet> testList = Arrays.asList(tweet3, tweet2, tweet1, tweet4, tweet5);
    	List<Tweet> writtenBy = Filter.writtenBy(testList, "alyssa");
    	
    	assertEquals("expected list size equals to 3", 3, writtenBy.size());
    	assertTrue("expected correct listResult and order", writtenBy.get(0).equals(tweet3));
    	assertTrue("expected correct listResult and order", writtenBy.get(1).equals(tweet1));
    	assertTrue("expected correct listResult and order", writtenBy.get(2).equals(tweet4));
    	assertTrue("expected unchanged original tweets", testList.equals(originList));
    }
    
    /**
     * Testing writtenBy
     * covers : name of different case
     * 			results order
     * 			multiple input multiple results
     */
    @Test
    public void testWrittenByDifferentCase() {
    	List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet3, tweet2, tweet1, tweet4, tweet5), "BbiTDiddle");
    	
    	assertEquals("expected list size equals to 2", 2, writtenBy.size());
    	assertTrue("expected correct listResult and order", writtenBy.get(0).equals(tweet2));
    	assertTrue("expected correct listResult and order", writtenBy.get(1).equals(tweet5));
    }
    
    /**
     * Testing inTimespan
     * covers : empty tweets
     */
    @Test
    public void testInTimespanEmptyTweets() {
    	Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
    	List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(), new Timespan(testStart, testEnd));
    	
    	assertTrue("expected empty list", inTimespan.isEmpty());
    }
    
    /**
     * Testing inTimespan
     * covers : multiple tweets
     * 			single tweet
     * 			single result
     * 			
     */
    @Test
    public void testInTimespanSingleResult() {
    	Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet3), new Timespan(testStart, testEnd));
        
        assertEquals("expected list of single tweet", 1, inTimespan.size());
        assertTrue("expected correct tweet", inTimespan.contains(tweet1));
        
        inTimespan = Filter.inTimespan(Arrays.asList(tweet1), new Timespan(testStart, testEnd));
        assertEquals("expected list of single tweet", 1, inTimespan.size());
        assertTrue("expected correct tweet", inTimespan.contains(tweet1));
    }
    
    /**
     * Testing inTimespan
     * covers : multiple tweets
     * 			multiple results
     * 			same order
     * 			unchanged origin list
     * 			tweets.timestamp : in (timespan.start, timespan.end), =timespan.start,
	 * 							 =timespan.end, not in timespan
     */
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
        
        
        List<Tweet> originList = Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5);
        List<Tweet> testList = Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5);
        inTimespan = Filter.inTimespan(testList, new Timespan(d2, d3));
        assertTrue("expected correct tweets", inTimespan.size() == 3 && 
        		inTimespan.containsAll(Arrays.asList(tweet2, tweet3, tweet4)));
        assertTrue("expected same order", inTimespan.indexOf(tweet2) == 0 && 
        		inTimespan.indexOf(tweet3) == 1 && inTimespan.indexOf(tweet4) == 2);
        assertTrue("expected unchanged original tweets", testList.equals(originList));
    }
    
    /**
     * Testing containing
     * covers : 
     * 		list order
     * 		begin with word, end with word, word in middle
	 * 		word is divided with space or not
	 * 		words in different case
	 * 		wordsList is empty, contains one word, contains more than one word
     */
    @Test
    public void testContaining() {
    	// one word, in middle
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected correct tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
        
        // empty wordlist
        containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList());
        assertTrue("expected empty list", containing.isEmpty());
        
        // multiple words, in the begining and in the end; words in different case
        List<Tweet> originList = Arrays.asList(tweet1, tweet2, tweet3);
        containing = Filter.containing(originList, Arrays.asList("#hype", "IS"));
        assertTrue("expected correct results", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertTrue("expected unchanged original list", originList.equals(Arrays.asList(tweet1, tweet2, tweet3)));
        
        // words divided with different space characters
        containing = Filter.containing(Arrays.asList(tweet4), Arrays.asList("has"));
        assertFalse("expected non-empty list", containing.isEmpty());
    }
    

    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */

}
