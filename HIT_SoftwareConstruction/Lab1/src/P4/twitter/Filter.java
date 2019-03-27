/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter consists of methods that filter a list of tweets for those matching a
 * condition.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Filter {

    /**
     * Find tweets written by a particular user.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param username
     *            Twitter username, required to be a valid Twitter username as
     *            defined by Tweet.getAuthor()'s spec.
     * @return all and only the tweets in the list whose author is username,
     *         in the same order as in the input list.
     */
    public static List<Tweet> writtenBy(List<Tweet> tweets, String username) {
        ArrayList<Tweet> filtTweets = new ArrayList<>();
        
        for(Tweet tweet : tweets) {
        	// check if username is equal
        	if(tweet.getAuthor().toLowerCase().equals(username.toLowerCase()))
        		filtTweets.add(tweet);
        }
        
        return filtTweets;
    }

    /**
     * Find tweets that were sent during a particular timespan.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param timespan
     *            timespan
     * @return all and only the tweets in the list that were sent during the timespan,
     *         in the same order as in the input list.
     */
    public static List<Tweet> inTimespan(List<Tweet> tweets, Timespan timespan) {
        ArrayList<Tweet> tweetList = new ArrayList<>();
        
        for(Tweet tweet : tweets) {
        	Instant tweetTime = tweet.getTimestamp();
        	// add tweets in time range[timespan.start, timespan.end]
        	if((tweetTime.isAfter(timespan.getStart()) && tweetTime.isBefore(timespan.getEnd()))
        		|| tweetTime.equals(timespan.getStart()) || tweetTime.equals(timespan.getEnd())
        		)
        		tweetList.add(tweet);
        }
        
        return tweetList;
    }

    /**
     * Find tweets that contain certain words.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param words
     *            a list of words to search for in the tweets. 
     *            A word is a nonempty sequence of nonspace characters.
     * @return all and only the tweets in the list such that the tweet text (when 
     *         represented as a sequence of nonempty words bounded by space characters 
     *         and the ends of the string) includes *at least one* of the words 
     *         found in the words list. Word comparison is not case-sensitive,
     *         so "Obama" is the same as "obama".  The returned tweets are in the
     *         same order as in the input list.
     */
    public static List<Tweet> containing(List<Tweet> tweets, List<String> words) {
        ArrayList<Tweet> tweetList = new ArrayList<>();
        
        for(Tweet tweet : tweets) {
        	String cont = tweet.getText().toLowerCase();
        	// check if word can be found in tweet
        	for(String word : words) {
        		// if one word found in content
        		if(containWord(cont, word.toLowerCase())) {
        			tweetList.add(tweet);
        			break;
        		}
        	}
        }
        
        return tweetList;
    }
    
    /**
     * check if content has word. word and other content should be divided with space;
     * word is case-insensitive
     * @param content content to search word in
     * @param word 
     * 		word to search for in the tweets. 
     *      A word is a nonempty sequence of nonspace characters.
     * @return whether content contains the word
     */
    private static boolean containWord(String content, String word) {
    	int index = content.indexOf(word);
   
    	// word not found
    	if(index < 0)
    		return false;
    	
    	// not begin with word and has no whitespace before the word
    	if(index != 0 && !Character.isWhitespace(content.charAt(index-1))) {
    		return false;
    	}
    	// not end with word and has no whitespace after the word
    	if(index + word.length() != content.length() &&
    			!Character.isWhitespace(content.charAt(index + word.length()))) {
    		return false;
    	}
    	
    	return true;
    }
}
