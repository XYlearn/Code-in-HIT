import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * class to build RoutePlanner
 * 
 * @author 
 */
public class RoutePlannerBuilder {

	private static final String regex = "(.*?),([\\d\\.-]+),([\\d\\.-]+),([\\d]+)";

	private RoutePlannerBuilder() {
		
	}

	/**
	 * get RoutePlanner from file
	 * 
	 * @return RoutePlanner instance
	 * @throws IOException
	 *             throw if file not exist or can't read
	 */
	public static RoutePlanner buildRoutePlanner(File file) throws IOException {
		RouteGraph graph = new RouteGraph();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

		String line;

		StopTime lastStopTime = null;
		StopTime nextStopTime = null;
		while ((line = bufferedReader.readLine()) != null) {
			if (line.trim().isEmpty())
				continue;
			// new bus route
			if (!Pattern.matches(regex, line)) {
				lastStopTime = null;
			}
			// set path
			else if (lastStopTime == null) {
				lastStopTime = getStopTime(line);
			} else {
				nextStopTime = getStopTime(line);
				graph.set(lastStopTime, nextStopTime, nextStopTime.getTime() - lastStopTime.getTime());
				lastStopTime = nextStopTime;
			}
		}
		//set path between same stop with different time
		for(StopTime last : graph.vertices()) {
			for(StopTime next : graph.vertices()) {
				if(last.equals(next))
					continue;
				if(last.stopEquals(next)) {
					int waittime = next.getTime() - last.getTime();
					if(waittime > 0 && waittime <= 1200)
						graph.set(last, next, waittime);
				}
			}
		}
		
		bufferedReader.close();

		return new TransitRoutePlanner(graph);
	}

	/**
	 * get StopTime from given String
	 * 
	 * @param info
	 *            string to parse
	 * @return StopTime parse from info. null if info has wrong format
	 */
	private static StopTime getStopTime(String info) {
		Matcher matcher = Pattern.compile(regex).matcher(info);
		if (!matcher.matches())
			return null;
		String name = matcher.group(1);
		double latitude = Double.valueOf(matcher.group(2));
		double longtitude = Double.valueOf(matcher.group(3));
		int time = Integer.valueOf(matcher.group(4));
		return new StopTime(name, latitude, longtitude, time);
	}

}
