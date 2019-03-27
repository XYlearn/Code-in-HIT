/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /*
     * getTimespan Testing strategy
     * Partition the inputs as follows:
     * tweets.length() : 0, 1, 2, >2 
     * tweets time 	   : equal, not equal
     * tweets order	   : in order, not in order
     */
	
	/**
	 * getMentionedUsers Testing strategy
	 * Partition the text area of tweet as follows:
	 * mentionedUser position : beginning, end, middle
	 * mentionedUser number   : 0, 1, >=2
	 * 
	 * partition the twitters.size() as follows:
	 * twitter.size() = 0, 1, >=2
	 * 
	 * Mixed case should be covered
	 * text with @ is not mentioning should be covered
	 */
    
	private static final Instant d0 = Instant.ofEpochSecond(0);
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-11-23T10:00:00Z");
    private static final Instant d4 = Instant.parse("2017-02-18T10:59:59Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "@coser rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "coser", "I'm coser", d3);
    private static final Tweet tweet4 = new Tweet(4, "alyssa", "@DOG has words to say @dog", d4);
    private static final Tweet tweet5 = new Tweet(5, "bbitdiddle", "@stu.hit.edu.cn is @coser 's email name", d4);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /**
     * Testing getTimespan
     * covers tweets.length() = 2
     * 		  tweets time equal, not equal
     */
    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
        
        timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet1));
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d1, timespan.getEnd());
    }
    
    /**
     * Testing getTimespan
     * covers tweets.length = 0, 1
     */
    @Test
    public void testGetTimeSpanOneOrZeroTweet() {
    	// tweets.length = 1
    	Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1));
    	assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d1, timespan.getEnd());
        
        // tweets.length = 0
        timespan = Extract.getTimespan(new ArrayList<Tweet>());
        assertEquals("should return Timespan(unixtimestamp0,unixtimestamp0)", d0, timespan.getStart());
        assertEquals("should return Timespan(unixtimestamp0,unixtimestamp0)", d0, timespan.getEnd());
    }
    
    /**
     * Testing getTimespan
     * covers tweets.length() > 2
     * 		  tweets in time order and not in time order
     */
    @Test
    public void testGetTimeSpanMoreThanTwoTweet() {
    	// in time order
    	Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2, tweet3, tweet4));
    	assertEquals("expected start", d1, timespan.getStart());
    	assertEquals("expected end", d4, timespan.getEnd());
    	
    	// not in time order
    	timespan = Extract.getTimespan(Arrays.asList(tweet3, tweet2, tweet1, tweet4));
    	assertEquals("expected start", d1, timespan.getStart());
    	assertEquals("expected end", d4, timespan.getEnd());
    }
    
    /**
     * Testing getMentionedUsers
     * covers mention 0 User
     * covers tweets.size = 0
     */
    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        assertTrue("expected empty set", mentionedUsers.isEmpty());
        
        mentionedUsers = Extract.getMentionedUsers(new ArrayList<>());
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }
    
    /**
     * Testing getMentionedUsers
     * covers mention 1 User
     * 		  mention User in the beginnig and middle
     * 		  text with @ is not mentioning
     */
    @Test
    public void testGetMentionedUsersOneMentioned() {
    	Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet2));
    	assertEquals("expected one element set", 1, mentionedUsers.size());
    	assertTrue("expected correct Name", mentionedUsers.contains("coser"));
    	
    	mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet5));
    	assertEquals("expected one element set", 1, mentionedUsers.size());
    	assertTrue("expected correct Name", mentionedUsers.contains("coser"));
    }
    
    /**
     * Testing getMentionedUsers
     * covers mention >2 Users
     * 		  mention User in the end
     * 		  mentioned usernames is in different cases
     * 		  
     */
    @Test 
    public void testGetMentionedUsersManyMentioned() {
    	Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet2, tweet3, tweet4));
    	assertEquals("expected two element set", 2, mentionedUsers.size());
    	for(String username : mentionedUsers) {
    		assertTrue("expected correct elements", username.toLowerCase().equals("dog") || username.equals("coser"));
    	}
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */

}
