package application;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import exception.CommandException;
import graph.GraphPoet;
import helper.AppParseCommandHelper;
import util.AppContext;
import util.ParseUtil;

public class GraphPoetApp {

	public static void main(String[] args) {
		registerCommands();
		Application.application(args);
	}

	/**
	 * register all additional commands used in GraphPoetApp
	 */
	private static void registerCommands() {
		try {
			// build_poem graphName pathname
			String[] syntax = new String[] { "build_poet", "\\w+", "[\\w\\./\\\\]+" };
			Method method = GraphPoetApp.class.getDeclaredMethod("executeBuildPoet", AppContext.class, String.class);
			AppParseCommandHelper.registerCommand(syntax, method);

			// poem pathname
			syntax = new String[] { "poem", "[\\w\\./\\\\]+" };
			method = GraphPoetApp.class.getDeclaredMethod("executePoem", AppContext.class, String.class);
			AppParseCommandHelper.registerCommand(syntax, method);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * parse and execute buld_poet command
	 * 
	 * @param context
	 *            AppContext
	 * @param command
	 *            command to execute
	 * @exception CommandException
	 *                throws when exception occurs during command execution
	 */
	public static void executeBuildPoet(AppContext context, String command) throws CommandException {
		String[] args = command.trim().split("\\s+");
		String name = args[1];
		// get words
		String pathname = args[2];
		File file = new File(pathname);
		if (!file.exists()) {
			String logText = "File \"" + pathname + "\" Not Exists";
			CommandException exception = new CommandException(logText);
			exception.setStartPos(command.indexOf(pathname));
			throw exception;
		}

		try {
			context.setGraph(new GraphPoet(name, pathname));
		} catch (IOException e) {
			String logText = "Fail to Read from File \"" + pathname + "\"";
			CommandException exception = new CommandException(logText);
			exception.setStartPos(command.indexOf(pathname));
			throw exception;
		}
	}

	/**
	 * parse and execute poem command. poem generates poem from given file
	 * 
	 * @param context
	 *            AppContext
	 * @param command
	 *            command to execute
	 * @exception CommandException
	 *                throws when exception occurs during command execution
	 */
	public static void executePoem(AppContext context, String command) throws CommandException {
		// check the Graph type
		if (!(context.getGraph() instanceof GraphPoet)) {
			String logText = "The Graph is Not a GraphPoet";
			CommandException exception = new CommandException(logText);
			throw exception;
		}
		GraphPoet graph = (GraphPoet) context.getGraph();

		// check the file's existence
		String pathname = command.trim().split("\\s+")[1];
		File file = new File(pathname);
		if (!file.exists()) {
			String logText = "File \"" + pathname + "\" Not Exists";
			CommandException exception = new CommandException(logText);
			exception.setStartPos(command.indexOf(pathname));
			throw exception;
		}
		// try to read content from file
		String content = null;
		try {
			content = ParseUtil.readFromFile(file);
		} catch (IOException e) {
			String logText = "Fail to Read from File \"" + pathname + "\"";
			CommandException exception = new CommandException(logText);
			exception.setStartPos(command.indexOf(pathname));
			throw exception;
		}

		// get poem
		String poem = graph.poem(content);
		AppParseCommandHelper.output(poem);
	}
}
