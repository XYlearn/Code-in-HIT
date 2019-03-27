import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
	private static Stop src;
	private static Stop dst;
	private static int time;
	private static Scanner scanner = new Scanner(System.in);
	private static String pathname = "rsrc/test.txt";
	private static RoutePlanner planner;

	static {
		// load from file
		try {
			planner = load(pathname);
		} catch (IOException e) {
			System.out.println("[-] Fail to open file");
			System.exit(0);
		}
	}

	/**
	 * main function
	 * @param args args
	 */
	public static void main(String[] args) {
		while (true) {
			try {
				src = getSrc();
				dst = getDst();
				time = getTime();
			} catch (Exception e) {
				continue;
			}
			Itinerary itinerary = planner.computeRoute(src, dst, time);
			System.out.println(itinerary.getInstructions());
		}
	}

	/**
	 * load from file and build routePlanner
	 * @param pathname
	 * @return routePlanner built
	 * @throws IOException throw if can;t access pathname
	 */
	private static RoutePlanner load(String pathname) throws IOException {
		File file = new File(pathname);
		RoutePlanner routePlanner = RoutePlannerBuilder.buildRoutePlanner(file);
		return routePlanner;
	}

	/**
	 * let user input departure place
	 * @return departure stop user input
	 */
	private static Stop getSrc() {
		while (true) {
			System.out.println("Please Input departure place");
			System.out.print(">> ");
			String departure = scanner.nextLine().trim();
			List<Stop> stops = planner.findStopsBySubstring(departure);
			if (stops.isEmpty())
				continue;
			// directly return stop if there is no ambiguous meaning
			else if (stops.size() == 1)
				return stops.get(0);
			// else let user to choose
			else
				return chooseStop(stops);
		}
	}

	/**
	 * let user input destination
	 * @return destination stop user input
	 */
	private static Stop getDst() {
		System.out.println("Please Input destination");
		while (true) {
			System.out.print(">> ");
			String destination = scanner.nextLine().trim();
			List<Stop> stops = planner.findStopsBySubstring(destination);
			if (stops.isEmpty())
				continue;
			// directly return stop if there is no ambiguous meaning
			else if (stops.size() == 1)
				return stops.get(0);
			// else let user to choose
			else
				return chooseStop(stops);
		}
	}

	/**
	 * let user input time
	 * @return time inputed
	 */
	private static int getTime() {
		while (true) {
			try {
				System.out.println("Please Input current Time");
				System.out.print(">> ");
				return Integer.valueOf(scanner.nextLine().trim());
			} catch (Exception e) {
				continue;
			}
		}
	}

	/**
	 * let user to choose stop from stop list
	 * @param stops stop list to choose from
	 * @return chosen stop
	 */
	private static Stop chooseStop(List<Stop> stops) {
		int ind = 0;
		while (true) {
			for (Stop stop : stops) {
				System.out.format("%d. %s (%f, %f)\n", ind++, stop.getName(), stop.getLatitude(), stop.getLatitude());
			}
			System.out.print("choice>> ");
			try {
				int choice = scanner.nextInt();
				return stops.get(choice - 1);
			} catch (Exception e) {
				continue;
			}
		}
	}
}
