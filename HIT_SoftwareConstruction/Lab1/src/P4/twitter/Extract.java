/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract consists of methods that extract information from a list of tweets.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     *         return Timespan whose start=end=unixtimestamp0 if list is empty
     */
    public static Timespan getTimespan(List<Tweet> tweets) {        
        // tweets.size() = 0
        if(tweets.isEmpty()) {
        	Instant instant = Instant.ofEpochSecond(0);
        	return new Timespan(instant, instant);
        }
        // tweets.size() = 1
        else if(tweets.size() == 1) {
        	Instant instant = tweets.get(0).getTimestamp();
        	return new Timespan(instant, instant);
        }
        
        // tweets.size() >= 2
        long startTimeStamp = Long.MAX_VALUE;
        long endTimeStamp = Long.MIN_VALUE;
        for(Tweet tweet : tweets) {
        	long timeStamp = tweet.getTimestamp().getEpochSecond();
        	if(timeStamp < startTimeStamp)
        		startTimeStamp = timeStamp;
        	if(timeStamp > endTimeStamp)
        		endTimeStamp = timeStamp;
        }
        return new Timespan(Instant.ofEpochSecond(startTimeStamp), 
        		Instant.ofEpochSecond(endTimeStamp));
    }

    /**
     * Get usernames mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like bitdiddle@mit.edu does NOT 
     *         contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        Set<String> set = new HashSet<String>();
        
        // Use getMentionedUsers for single tweet
        for(Tweet tweet : tweets) {
        	set.addAll(getMentionedUsers(tweet));
        }
        
        return set;
    }
    
    /**
     * Get usernames mentioned in single tweet
     * @param tweet
     * 			list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     */
    public static Set<String> getMentionedUsers(Tweet tweet) {
    	Set<String> set = new HashSet<String>();
    	
    	String text = tweet.getText();
    	text = text.replaceAll(" ", "  ");
    	// use regex to find all mentioned names
    	String pattern = "(?<=^|[^\\w-])(?:@)([\\w-]+)(?:$|[^\\w-])";
    	Pattern compiledPattern = Pattern.compile(pattern);
    	Matcher matcher = compiledPattern.matcher(text);
    	while(matcher.find()) {
    		// should convert to same case
    		set.add(matcher.group(1).toLowerCase());
    	}
    	
    	return set;
    }

}
