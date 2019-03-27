package helper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import util.AppContext;
import util.CommandSyntax;
import util.ParseUtil;
import vertex.Vertex;

public class AppParseCommandHelper extends ParseCommandHelper {

	protected static final Set<String> buildCommands = new HashSet<>(Arrays.asList("build"));
	protected static final Set<String> operateCommands = new HashSet<>(Arrays.asList("vertex", "edge", "hyperedge"));
	protected static final Set<String> calculateCommands = new HashSet<>(
			Arrays.asList("centrality", "distance", "eccentricity", "radius", "diameter"));
	protected static final Set<String> visualCommands = new HashSet<>(Arrays.asList("show"));

	protected static final Set<String> allCommands = new HashSet<>();
	protected static final Set<String> commonCommands = new HashSet<>();
	static {
		commonCommands.addAll(buildCommands);
		commonCommands.addAll(operateCommands);
		commonCommands.addAll(calculateCommands);
		commonCommands.addAll(calculateCommands);
		commonCommands.addAll(visualCommands);

		allCommands.addAll(commonCommands);
	}

	protected static final Map<String, CommandSyntax> syntaxMap = new HashMap<>();
	protected static final Map<String, Method> methodMap = new HashMap<>();

	/**
	 * Register a command with syntax to AppParseCommandHelper
	 * 
	 * @param syntax
	 *            the syntax of command, elements of syntax can be regular
	 *            expressions
	 * @method method that receive two argments, the first of which is the
	 *         AppContext instance and the second is the command String
	 * @return return true if register successfully. if the syntax is invalid or the
	 *         command syntax already existed, return false
	 */
	public static boolean registerCommand(String[] syntax, Method method) {
		if (syntax.length < 1)
			return false;
		String mnem = syntax[0];
		CommandSyntax commandSyntax = new CommandSyntax(mnem, syntax);
		// fail if the command sytax already exists
		if (syntaxMap.containsValue(commandSyntax))
			return false;

		allCommands.add(mnem);
		syntaxMap.put(mnem, commandSyntax);
		methodMap.put(mnem, method);
		return true;
	}

	/**
	 * parse the command and execute if the command is common Command or
	 * registedCommand. if the command is not valid, the fail message will be logged
	 * 
	 * @param command
	 *            command to parse and execute
	 * @exception BadCommandException
	 *                throw when unrecognized command met
	 */
	public static void parseAndExecuteCommand(AppContext context, String command) throws BadCommandException {
		String mnem = ParseUtil.firstWord(command);
		if (commonCommands.contains(mnem)) {
			parseAndExecuteCommonCommand(context, command);
		} else if (allCommands.contains(mnem)) {
			try {
				parseAndExcuteAdditionalCommand(context, command);
			} catch (Exception e) {
				log(0, "Fail to Execute Registered Command");
				//e.printStackTrace();
			}
		} else {
			log(0, "Unknow Command " + mnem);
		}
	}

	/**
	 * parse and execute registered command
	 * 
	 * @param context
	 *            AppContext to execute command on
	 * @param command
	 *            command line to execute
	 * @throws BadCommandException
	 *             throw when unrecognized command met
	 */
	private static void parseAndExcuteAdditionalCommand(AppContext context, String command) throws BadCommandException {
		String mnem = ParseUtil.firstWord(command);
		CommandSyntax syntax = syntaxMap.get(mnem);
		Method method = methodMap.get(mnem);
		// if the syntax or method is unregistered
		if (null == syntax || null == method) {
			log(0, "Unregistered Command " + mnem);
			return;
		}

		// check if the command syntax is valid
		if (!syntax.match(command)) {
			log(0, "Invalid Syntax for Command " + mnem);
			return;
		}

		// try to execute command
		try {
			method.invoke(null, context, command);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			log(0, "Registered Method for Command " + mnem + " is Invalid");
			return;
		}
	}

	/**
	 * parse and execute inner(common) command
	 * 
	 * @param context
	 *            AppContext to execute command on
	 * @param command
	 *            command line to execute
	 * @throws BadCommandException
	 *             throws when the command is unrecognized
	 */
	private static void parseAndExecuteCommonCommand(AppContext context, String command) throws BadCommandException {
		String mnem = ParseUtil.firstWord(command);

		if (buildCommands.contains(mnem)) {
			if (null == context.getGraph())
				log(0, "Graph not Built");
			parseAndExecuteBuildCommand(context, command);
		} else if (operateCommands.contains(mnem)) {
			ParseCommandHelper.parseAndExecuteCommand(context.getGraph(), command);
		} else if (calculateCommands.contains(mnem)) {
			parseAndExecuteCalculateCommand(context, command);
		} else if (visualCommands.contains(mnem)) {
			parseAndExecuteVisualCommand(context, command);
		} else {
			throw new BadCommandException();
		}

	}

	/**
	 * parse and execute BuildCommand
	 * 
	 * @param context
	 *            app context
	 * @param command
	 *            command to execute
	 * @throws BadCommandException
	 *             throw when unrecognized command met
	 */
	private static void parseAndExecuteBuildCommand(AppContext context, String command) throws BadCommandException {
		String[] args = command.trim().split("\\s+");
		if (args.length < 2) {
			log(0, "No Pathname Specified");
			return;
		}
		String pathname = args[1];
		try {
			context.setGraph(ParseCommandHelper.buildGraphFromFile(pathname));
		} catch (IOException e) {
			log(0, "Invalid Pathname " + pathname);
		}
	}

	/**
	 * parse and execute CalculateCommand
	 * 
	 * @param context
	 *            app context
	 * @param command
	 *            command to execute
	 * @exception BadCommandException
	 *                throw when unrecognized command met
	 */
	private static void parseAndExecuteCalculateCommand(AppContext context, String command) throws BadCommandException {
		if (null == context.getGraph())
			log(0, "Graph not Built");
		String[] args = command.trim().split("\\s+");
		Vertex vertex = null;
		double centrality = 0;
		int argLength = args.length;

		if (argLength == 0)
			throw new BadCommandException();
		switch (args[0]) {
		// calculate centrality
		case "centrality":
			// centrality
			if (argLength == 1) {
				centrality = GraphMetrics.degreeCentrality(context.getGraph());
				System.out.println(centrality);
				return;
			}

			switch (args[1]) {
			case "--closeness":
				if (args.length < 3) {
					log(0, "No Vertex Specified");
					return;
				} else if (argLength == 3) {
					// centrality --closeness vertex
					vertex = Vertex.common(args[2]);
					centrality = GraphMetrics.closenessCentrality(context.getGraph(), vertex);
					System.out.println(centrality);
					break;
				} else {
					log(0, "Invalid Arguments Number");
					return;
				}
			case "--betweenness":
				if (args.length < 3) {
					log(0, "No Vertex Specified");
					return;
				} else if (argLength == 3) {
					// centrality --betweenness vertex
					vertex = Vertex.common(args[2]);
					centrality = GraphMetrics.betweennessCentrality(context.getGraph(), vertex);
					System.out.println(centrality);
					return;
				} else {
					log(0, "Invalid Arguments Number");
					return;
				}
			case "--degree":
				// centrality --degree vertex
				if (argLength == 3) {
					vertex = Vertex.common(args[2]);
					centrality = GraphMetrics.betweennessCentrality(context.getGraph(), vertex);
					System.out.println(centrality);
					return;
				}
				// centrality --degree
				else if (argLength < 3) {
					centrality = GraphMetrics.degreeCentrality(context.getGraph());
					System.out.println(centrality);
					return;
				} else {
					log(0, "Invalid argument number");
					return;
				}
			case "--indegree":
				// centrality --indegree vertex
				if (argLength == 3) {
					vertex = Vertex.common(args[2]);
					centrality = GraphMetrics.inDegreeCentrality(context.getGraph(), vertex);
					System.out.println(centrality);
					return;
				} else {
					System.out.println("Invalid Arguments Number");
					return;
				}
			case "--outdegree":
				// centrality --outdegree vertex
				if (argLength == 3) {
					vertex = Vertex.common(args[2]);
					centrality = GraphMetrics.outDegreeCentrality(context.getGraph(), vertex);
					System.out.println(centrality);
					return;
				} else {
					System.out.println("Invalid Arguments Number");
					return;
				}

			default:
				if (argLength == 2) {
					vertex = Vertex.common(args[1]);
					centrality = GraphMetrics.betweennessCentrality(context.getGraph(), vertex);
					System.out.println(centrality);
					return;
				} else {
					log(0, "Invalid Arguments");
					return;
				}
			}
			break;
		// calculate eccentrality
		case "eccentrality":
			// eccentrality vertex
			if (argLength == 2) {
				vertex = Vertex.common(args[1]);
				double eccentrality = GraphMetrics.eccentricity(context.getGraph(), vertex);
				System.out.println(eccentrality);
				return;
			} else {
				log(0, "Invalid Arguments Number");
			}
			break;
		case "distance":
			if (argLength == 3) {
				Vertex v1 = Vertex.common(args[1]);
				Vertex v2 = Vertex.common(args[2]);
				double distance = GraphMetrics.distance(context.getGraph(), v1, v2);
				System.out.println(distance);
				return;
			}
			break;
		case "radius":
			if (argLength != 1) {
				log(0, "Invalid Arguments Number");
			} else {
				double radius = GraphMetrics.radius(context.getGraph());
				System.out.println(radius);
				return;
			}
			break;
		case "diameter":
			if (argLength != 1) {
				log(0, "Invalid Arguments Number");
			} else {
				double diameter = GraphMetrics.diameter(context.getGraph());
				System.out.println(diameter);
				return;
			}
			break;
		default:
			throw new BadCommandException();
		}
	}

	/**
	 * parse and execute VisualCommand
	 * 
	 * @param context
	 *            app context
	 * @param command
	 *            command to execute
	 * @throws BadCommandException
	 *             throw when unrecognized command met
	 */
	private static void parseAndExecuteVisualCommand(AppContext context, String command) throws BadCommandException {
		if (null == context.getGraph())
			log(0, "Graph not Built");
		String mnem = ParseUtil.firstWord(command);
		switch (mnem) {
		case "show":
			GraphVisualizationHelper.visualize(context.getGraph());
			break;
		default:
			throw new BadCommandException();
		}
	}

	/**
	 * log information in parse process
	 * 
	 * @param level
	 *            log level
	 * @param info
	 *            information to log
	 */
	public static void log(int level, String info) {
		ParseCommandHelper.log(level, info);
	}
}
