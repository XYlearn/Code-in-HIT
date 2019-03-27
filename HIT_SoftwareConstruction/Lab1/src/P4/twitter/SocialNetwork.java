	/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.TreeMap;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> graphMap = new HashMap<>();
        
        // guess according to @-mentioned users
        for(Tweet tweet : tweets) {
        	// use Extract method to get mentioned users
        	Set<String> mentionedUsers= Extract.getMentionedUsers(tweet);
        	// all username is stored in lowercase
        	String authorName = tweet.getAuthor().toLowerCase();
        	if(mentionedUsers.isEmpty())
        		continue;
        	if(graphMap.containsKey(authorName)) {
        		// merge with existing set
        		graphMap.get(authorName).addAll(mentionedUsers);
        	} else {
        		graphMap.put(authorName, mentionedUsers);
        	}
        }
        	
        return graphMap;
    }
    
    /**
     * a smarter way to get follow graph
     * persume users that have over than 2 tags follows each other 
     * @param tweets tweets data
     * @return a social network as defined above
     */
    public static Map<String, Set<String>> smartGuessFollowsGraph(List<Tweet> tweets) {
    	Map<String, Set<String>> graphMap = guessFollowsGraph(tweets);
    	
    	HashMap<String, Set<String>> tagMap = getTagSharers(tweets);
    	
    	int sizeThreshold = (int) Math.round(tweets.size() / 200.0);
    	Map<String, Set<String>> tempGraphMap = 
    			guessFollowGraphUnpopularTags(tagMap, sizeThreshold);
    	
    	// merge
    	mergeFollowsGraph(graphMap, tempGraphMap);
    	
    	return graphMap;
    }
    
    /**
     * guess users follow network according to unpopular Tags
     * persume users share a hashtag that isn¡¯t otherwise 
     * popular(less than 0.5% * tweets.size users share the tag) in the dataset
     * @param tagMap map from tag to sharers
     * @param sizeThreshold unpopular tag threshold
     * @return persumed followGraph
     */
    private static Map<String, Set<String>> 
    guessFollowGraphUnpopularTags(Map<String, Set<String>> tagMap, int sizeThreshold) {
    	Map<String, Set<String>> graphMap = new HashMap<>();
    	
    	for(Map.Entry<String, Set<String>> entry : tagMap.entrySet()) {
    		Set<String> userSet = entry.getValue();
    		if(userSet.size() > sizeThreshold)
    			continue;
    		for(String user1 : userSet) {
    			for(String user2 : userSet) {
    				if(user1 == user2)
    					continue;
    				// user1 and user2 follow each other
    				if(!graphMap.containsKey(user1))
    					graphMap.put(user1, new HashSet<>());
    				graphMap.get(user1).add(user2);
    				if(!graphMap.containsKey(user2))
    					graphMap.put(user2, new HashSet<>());
    				graphMap.get(user2).add(user1);
    			}
    		}
    	}
    	
    	return graphMap;
    }
    
    /**
     * get map from tag and its sharers according to tweets content
     * @param tweets tweets data
     * @return map from tag and its sharers
     */
    private static HashMap<String, Set<String>> getTagSharers(List<Tweet> tweets) {
    	HashMap<String, Set<String>> tagMap = new HashMap<>();
    	
    	// get all tag and tagUsers(users that have the tag)
    	for(Tweet tweet : tweets) {
    		String author = tweet.getAuthor().toLowerCase();
    		// find all tags
    		List<String> tagList = getTags(tweet.getText());
    		for(String tag : tagList) {
    			if(!tagMap.containsKey(tag))
    				tagMap.put(tag, new HashSet<>());
    			tagMap.get(tag).add(author);
    		}
    	}
    	
    	return tagMap;
    }
    
    /**
     * merge graph2 to graph1
     * @param graphMap source graph to merge
     * @param tempGraphMap dest graph to merge 
     * @return
     */
    private static Map<String, Set<String>> 
    mergeFollowsGraph(Map<String, Set<String>> graphMap, Map<String, Set<String>> tempGraphMap) {
    	for(Map.Entry<String, Set<String>> entry : tempGraphMap.entrySet()) {
    		String username = entry.getKey();
    		Set<String> followers = entry.getValue();
    		if(!graphMap.containsKey(username))
    			graphMap.put(username, followers);
    		else
    			graphMap.get(username).addAll(followers);
    	}
    	
    	return graphMap;
    }
    
    
    /**
     * find tags in string
     * persume tags are splited with other words in space character
     * 	and only contain words, digits, and '_'
     * @param s text contains tags
     * @return all tags found in s
     */
    private static List<String> getTags(String s) {
    	ArrayList<String> tagList = new ArrayList<>();
    	
    	// to make space character can be shared by two tags
    	String text = s.replaceAll("\\s", "  ");
    	
    	String pattern = "(?:^|\\s)(?:#)(\\w+?)(?:$|\\s)";
    	Pattern compiledPattern = Pattern.compile(pattern);
    	Matcher matcher = compiledPattern.matcher(text);
    	while(matcher.find()) {
    		tagList.add(matcher.group(1).toLowerCase());
    	}
    	return tagList;
    }
    

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
    	ArrayList<String> influencerList = new ArrayList<>();;
    	TreeMap<String, Integer> followerNumberMap = new TreeMap<>();
    	for(Map.Entry<String, Set<String>> entry : followsGraph.entrySet()) {
    		Set<String> followedUsers = entry.getValue();
    		
    		// increase by one
    		for(String followedUser : followedUsers) {
    			String followedUserLowerCase = followedUser.toLowerCase();
    			if(followerNumberMap.containsKey(followedUserLowerCase))
        			followerNumberMap.put(followedUserLowerCase, followerNumberMap.get(followedUserLowerCase) + 1);
        		else
        			followerNumberMap.put(followedUserLowerCase, 1);
    		}
    	}
    	
    	// get ordered list
    	ArrayList<Map.Entry<String, Integer>> tempList = new ArrayList<>(followerNumberMap.entrySet());

    	Collections.sort(tempList, new Comparator<Map.Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
    		
    	});
    	for(Entry<String, Integer> entry : tempList) {
    		influencerList.add(entry.getKey());
    	}
    	
    	return influencerList;
    }

}
