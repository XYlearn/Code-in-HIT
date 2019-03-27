package application;

import java.lang.reflect.Method;

import exception.CommandException;
import graph.SocialNetwork;
import helper.AppParseCommandHelper;
import util.AppContext;
import vertex.Vertex;

public class SocialNetworkApp {

	public static void main(String[] args) {
		registerCommands();
		Application.application(args);
	}

	/**
	 * register all additional commands used in GraphPoetApp
	 */
	private static void registerCommands() {
		try {
			String[] syntax = new String[] { "influence", "\\w+" };
			Method method = SocialNetworkApp.class.getDeclaredMethod("executeInfluence", AppContext.class,
					String.class);
			AppParseCommandHelper.registerCommand(syntax, method);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * execute influence command
	 * 
	 * @param context
	 *            AppContext
	 * @param command
	 *            command to execute
	 * @throws CommandException
	 *             throw when command execution failed
	 */
	public static void executeInfluence(AppContext context, String command) throws CommandException {
		if ((context.getGraph() instanceof SocialNetwork)) {
			String logText = "The Graph is Not a SocialNetwork Graph";
			CommandException exception = new CommandException(logText);
			throw exception;
		}

		String[] args = command.trim().split("\\s+");
		String vertexLabel = args[1];
		SocialNetwork graph = (SocialNetwork) context.getGraph();
		double influence = graph.vertexWeight(Vertex.common(vertexLabel));
		System.out.println(influence);
	}

}
