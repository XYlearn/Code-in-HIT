package helper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import exception.CommandException;
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
	 * @exception CommandException
	 *                throw when fail to parse or execute command
	 */
	public static void parseAndExecuteCommand(AppContext context, String command) throws CommandException {
		String mnem = ParseUtil.firstWord(command);
		if (commonCommands.contains(mnem)) {
			parseAndExecuteCommonCommand(context, command);
		} else if (allCommands.contains(mnem)) {
			parseAndExcuteAdditionalCommand(context, command);
		} else {
			String logText = "Unknow Command \"" + mnem + "\".";
			CommandException exception = new CommandException(logText);
			exception.setLineNo(0);
			exception.setStartPos(0);
			exception.setCommand(command);
			logger.info(exception.getLogText());
			throw exception;
		}
	}

	/**
	 * parse and execute registered command
	 * 
	 * @param context
	 *            AppContext to execute command on
	 * @param command
	 *            command line to execute
	 * @throws CommandException
	 *             throw when fail to parse or execute command
	 */
	private static void parseAndExcuteAdditionalCommand(AppContext context, String command) throws CommandException {
		String mnem = ParseUtil.firstWord(command);
		CommandSyntax syntax = syntaxMap.get(mnem);
		Method method = methodMap.get(mnem);
		// if the syntax or method is unregistered
		if (null == syntax || null == method) {
			String logText = "Unregistered Command \"" + mnem + "\".";
			CommandException exception = new CommandException(logText);
			exception.setLineNo(0);
			exception.setStartPos(0);
			exception.setCommand(command);
			logger.info(exception.getLogText());
			throw exception;
		}

		// check if the command syntax is valid
		if (!syntax.match(command)) {
			String logText = "Invalid Syntax for Command \"" + mnem + "\".";
			CommandException exception = new CommandException(logText);
			exception.setLineNo(0);
			exception.setStartPos(0);
			exception.setCommand(command);
			logger.info(exception.getLogText());
			throw exception;
		}

		// try to execute command
		try {
			method.invoke(null, context, command);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			String logText = "Registered Method for Command \"" + mnem + "\" is Invalid";
			CommandException exception = new CommandException(logText);
			exception.setLineNo(0);
			exception.setStartPos(0);
			exception.setCommand(command);
			logger.info(exception.getLogText());
			throw exception;
		}
	}

	/**
	 * parse and execute inner(common) command
	 * 
	 * @param context
	 *            AppContext to execute command on
	 * @param command
	 *            command line to execute
	 * @throws CommandException
	 *             throws when the command is unrecognized
	 */
	private static void parseAndExecuteCommonCommand(AppContext context, String command) throws CommandException {
		String mnem = ParseUtil.firstWord(command);

		if (buildCommands.contains(mnem)) {
			if (null == context.getGraph() && !mnem.equals("build")) {
				String logText = "Graph not Built";
				CommandException exception = new CommandException(logText);
				exception.setLineNo(0);
				exception.setStartPos(0);
				exception.setCommand(command);
				logger.info(exception.getLogText());
				throw exception;
			}
			parseAndExecuteBuildCommand(context, command);
		} else if (operateCommands.contains(mnem)) {
			ParseCommandHelper.parseAndExecuteCommand(context.getGraph(), command);
		} else if (calculateCommands.contains(mnem)) {
			parseAndExecuteCalculateCommand(context, command);
		} else if (visualCommands.contains(mnem)) {
			parseAndExecuteVisualCommand(context, command);
		} else {
			String logText = "Unknown Command";
			CommandException exception = new CommandException(logText);
			exception.setCommand(command);
			exception.setLineNo(0);
			exception.setStartPos(0);
			logger.info(exception.getLogText());
			throw exception;
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
	private static void parseAndExecuteBuildCommand(AppContext context, String command) throws CommandException {
		String[] args = command.trim().split("\\s+");
		if (args.length < 2) {
			String logText = "No Pathname Specified";
			CommandException exception = new CommandException(logText);
			exception.setLineNo(0);
			exception.setStartPos(0);
			exception.setCommand(command);
			logger.info(exception.getLogText());
			throw exception;
		}
		String pathname = args[1];
		try {
			context.setGraph(ParseCommandHelper.buildGraphFromFile(pathname));
			logger.fine("Graph \"" + context.getGraph().getName() + "\" Is Built Successfully");
		} catch (IOException e) {
			String logText = "File \"" + pathname + "\" Can't be Opened or Read";
			CommandException exception = new CommandException(logText);
			exception.setLineNo(0);
			exception.setStartPos(0);
			exception.setCommand(command);
			logger.info(exception.getLogText());
			throw exception;
		}
	}

	/**
	 * parse and execute CalculateCommand
	 * 
	 * @param context
	 *            app context
	 * @param command
	 *            command to execute
	 * @exception CommandException
	 *                throw when fail to execute command
	 */
	private static void parseAndExecuteCalculateCommand(AppContext context, String command) throws CommandException {
		if (null == context.getGraph()) {
			String logText = "Graph not Built";
			CommandException exception = new CommandException(logText);
			exception.setLineNo(0);
			exception.setStartPos(0);
			exception.setCommand(command);
			logger.info(exception.getLogText());
			throw exception;
		}
		String[] args = command.trim().split("\\s+");
		Vertex vertex = null;
		double centrality = 0;
		int argLength = args.length;

		// empty command line; do nothing
		if (argLength == 0) {
			return;
		}
		switch (args[0]) {
		// calculate centrality
		case "centrality":
			// centrality
			if (argLength == 1) {
				centrality = GraphMetrics.degreeCentrality(context.getGraph());
				output(centrality);
				return;
			}

			switch (args[1]) {
			case "--closeness":
				if (args.length < 3) {
					String logText = "No Vertex Specified";
					CommandException exception = new CommandException(logText);
					exception.setLineNo(0);
					exception.setStartPos(0);
					exception.setCommand(command);
					logger.info(exception.getLogText());
					throw exception;
				} else if (argLength == 3) {
					// centrality --closeness vertex
					vertex = Vertex.common(args[2]);
					centrality = GraphMetrics.closenessCentrality(context.getGraph(), vertex);
					output(centrality);
					break;
				} else {
					String logText = "Invalid Arguments Number";
					CommandException exception = new CommandException(logText);
					exception.setLineNo(0);
					exception.setStartPos(0);
					exception.setCommand(command);
					logger.info(exception.getLogText());
					throw exception;
				}
			case "--betweenness":
				if (args.length < 3) {
					String logText = "No Vertex Specified";
					CommandException exception = new CommandException(logText);
					exception.setLineNo(0);
					exception.setStartPos(0);
					exception.setCommand(command);
					logger.info(exception.getLogText());
					throw exception;
				} else if (argLength == 3) {
					// centrality --betweenness vertex
					vertex = Vertex.common(args[2]);
					centrality = GraphMetrics.betweennessCentrality(context.getGraph(), vertex);
					output(centrality);
					return;
				} else {
					String logText = "Invalid Arguments Number";
					CommandException exception = new CommandException(logText);
					exception.setLineNo(0);
					exception.setStartPos(0);
					exception.setCommand(command);
					logger.info(exception.getLogText());
					throw exception;
				}
			case "--degree":
				// centrality --degree vertex
				if (argLength == 3) {
					vertex = Vertex.common(args[2]);
					centrality = GraphMetrics.betweennessCentrality(context.getGraph(), vertex);
					output(centrality);
					return;
				}
				// centrality --degree
				else if (argLength < 3) {
					centrality = GraphMetrics.degreeCentrality(context.getGraph());
					output(centrality);
					return;
				} else {
					String logText = "Invalid Arguments Number";
					CommandException exception = new CommandException(logText);
					exception.setLineNo(0);
					exception.setStartPos(0);
					exception.setCommand(command);
					logger.info(exception.getLogText());
					throw exception;
				}
			case "--indegree":
				// centrality --indegree vertex
				if (argLength == 3) {
					vertex = Vertex.common(args[2]);
					centrality = GraphMetrics.inDegreeCentrality(context.getGraph(), vertex);
					output(centrality);
					return;
				} else {
					String logText = "Invalid Arguments Number";
					CommandException exception = new CommandException(logText);
					exception.setLineNo(0);
					exception.setStartPos(0);
					exception.setCommand(command);
					logger.info(exception.getLogText());
					throw exception;
				}
			case "--outdegree":
				// centrality --outdegree vertex
				if (argLength == 3) {
					vertex = Vertex.common(args[2]);
					centrality = GraphMetrics.outDegreeCentrality(context.getGraph(), vertex);
					output(centrality);
					return;
				} else {
					String logText = "Invalid Arguments Number";
					CommandException exception = new CommandException(logText);
					exception.setLineNo(0);
					exception.setStartPos(0);
					exception.setCommand(command);
					logger.info(exception.getLogText());
					throw exception;
				}

			default:
				if (argLength == 2) {
					vertex = Vertex.common(args[1]);
					centrality = GraphMetrics.betweennessCentrality(context.getGraph(), vertex);
					output(centrality);
					return;
				} else {
					String logText = "Invalid Arguments Number";
					CommandException exception = new CommandException(logText);
					exception.setLineNo(0);
					exception.setStartPos(0);
					exception.setCommand(command);
					logger.info(exception.getLogText());
					throw exception;
				}
			}
			break;
		// calculate eccentrality
		case "eccentrality":
			// eccentrality vertex
			if (argLength == 2) {
				vertex = Vertex.common(args[1]);
				double eccentrality = GraphMetrics.eccentricity(context.getGraph(), vertex);
				output(eccentrality);
				return;
			} else {
				String logText = "Invalid Arguments Number";
				CommandException exception = new CommandException(logText);
				exception.setLineNo(0);
				exception.setStartPos(0);
				exception.setCommand(command);
				logger.info(exception.getLogText());
				throw exception;
			}
		case "distance":
			if (argLength == 3) {
				Vertex v1 = Vertex.common(args[1]);
				Vertex v2 = Vertex.common(args[2]);
				double distance = GraphMetrics.distance(context.getGraph(), v1, v2);
				output(distance);
				return;
			}
			break;
		case "radius":
			if (argLength != 1) {
				String logText = "Invalid Arguments Number";
				CommandException exception = new CommandException(logText);
				exception.setLineNo(0);
				exception.setStartPos(0);
				exception.setCommand(command);
				logger.info(exception.getLogText());
				throw exception;
			} else {
				double radius = GraphMetrics.radius(context.getGraph());
				output(radius);
				return;
			}
		case "diameter":
			if (argLength != 1) {
				String logText = "Invalid Arguments Number";
				CommandException exception = new CommandException(logText);
				exception.setLineNo(0);
				exception.setStartPos(0);
				exception.setCommand(command);
				logger.info(exception.getLogText());
				throw exception;
			} else {
				double diameter = GraphMetrics.diameter(context.getGraph());
				output(diameter);
				return;
			}
		default:
			String logText = "Unknow Command";
			CommandException exception = new CommandException(logText);
			exception.setLineNo(0);
			exception.setStartPos(0);
			exception.setCommand(command);
			logger.info(exception.getLogText());
			throw exception;
		}
	}

	/**
	 * parse and execute VisualCommand
	 * 
	 * @param context
	 *            app context
	 * @param command
	 *            command to execute
	 * @throws CommandException
	 *             throw when command execution failed
	 */
	private static void parseAndExecuteVisualCommand(AppContext context, String command) throws CommandException {
		if (null == context.getGraph()) {
			String logText = "Graph not Built";
			CommandException exception = new CommandException(logText);
			exception.setLineNo(0);
			exception.setStartPos(0);
			exception.setCommand(command);
			logger.info(exception.getLogText());
			throw exception;
		}
		String mnem = ParseUtil.firstWord(command);
		switch (mnem) {
		case "show":
			GraphVisualizationHelper.visualize(context.getGraph());
			break;
		default:
			String logText = "Unknown Command";
			CommandException exception = new CommandException(logText);
			exception.setLineNo(0);
			exception.setStartPos(0);
			exception.setCommand(command);
			logger.info(exception.getLogText());
			throw exception;
		}
	}

	/**
	 * output the result of command execution
	 * 
	 * @param result
	 *            result to output
	 */
	public static void output(Object result) {
		System.out.println(result);
	}
}
