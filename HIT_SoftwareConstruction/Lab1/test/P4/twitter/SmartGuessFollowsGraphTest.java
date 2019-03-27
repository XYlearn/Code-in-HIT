package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class SmartGuessFollowsGraphTest {

	/**
	 * Testing Strategies
	 * covers number of tag sharers : ceiling of 0.5% * tweets.size() 
	 * 								  a bit over than 0.5% * tweets.size()
	 */
	
	@Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
	
	@Test
	public void testSmartGuessFollowsGraph() {
		ArrayList<Tweet> tweets = new ArrayList<>();
		for(int i = 0; i < 5; i++) {
			tweets.add(new Tweet(i, String.valueOf(i), "#tag1", Instant.now()));
		}
		for(int i = 0; i < 995; i++) {
			tweets.add(new Tweet(i, String.valueOf(i), "#tag2", Instant.now()));
		}
		Map<String, Set<String>> graphMap = SocialNetwork.smartGuessFollowsGraph(tweets);
		assertFalse("expected non-empty graph", graphMap.isEmpty());
		assertTrue("expected correct Set size", graphMap.get("1").size()==4);
		
		tweets.clear();
		for(int i = 0; i < 10; i++) {
			tweets.add(new Tweet(i, String.valueOf(i), "#tag1", Instant.now()));
		}
		for(int i = 0; i < 995; i++) {
			tweets.add(new Tweet(i, String.valueOf(i), "#tag2", Instant.now()));
		}
		graphMap = SocialNetwork.smartGuessFollowsGraph(tweets);
		System.out.println(graphMap);
		assertTrue("expected empty graph", graphMap.isEmpty());
	}

}
