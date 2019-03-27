package application;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

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
	 */
	public static void executeBuildPoet(AppContext context, String command) {
		String[] args = command.trim().split("\\s+");
		String name = args[1];
		// get words
		String pathname = args[2];
		File file = new File(pathname);
		if (!file.exists()) {
			AppParseCommandHelper.log(0, "File " + pathname + " Not Exists");
			return;
		}

		try {
			context.setGraph(new GraphPoet(name, pathname));
		} catch (IOException e) {
			AppParseCommandHelper.log(0, "Fail to Read from File " + pathname);
			return;
		}
	}

	/**
	 * parse and execute poem command. poem generates poem from given file
	 * 
	 * @param context
	 *            AppContext
	 * @param command
	 *            command to execute
	 */
	public static void executePoem(AppContext context, String command) {
		// check the Graph type
		if (!(context.getGraph() instanceof GraphPoet)) {
			AppParseCommandHelper.log(0, "The Graph is Not a GraphPoet");
			return;
		}
		GraphPoet graph = (GraphPoet) context.getGraph();

		// check the file's existence
		String pathname = command.trim().split("\\s+")[1];
		File file = new File(pathname);
		if (!file.exists()) {
			AppParseCommandHelper.log(0, "File " + pathname + " Not Exists");
			return;
		}
		// try to read content from file
		String content = null;
		try {
			content = ParseUtil.readFromFile(file);
		} catch (IOException e) {
			AppParseCommandHelper.log(0, "Fail to Read from File " + pathname);
			return;
		}

		// get poem
		String poem = graph.poem(content);
		System.out.println(poem);
	}
}
