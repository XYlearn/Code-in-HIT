package application;

import java.util.Scanner;

import exception.CommandException;
import helper.AppParseCommandHelper;
import util.AppContext;

/**
 * Application Framework class.
 *
 */
public class Application {
	/**
	 * main while loop of application
	 * 
	 * @param args
	 *            main arguments
	 */
	public static void application(String[] args) {
		AppContext context = new AppContext(null);

		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.print(">> ");
			String command = scanner.nextLine();
			if(command.trim().isEmpty())
				continue;
			// check exist
			if (command.trim().equals("exit") || command.trim().equals("quit")) {
				scanner.close();
				System.exit(0);
			}
			try {
				AppParseCommandHelper.parseAndExecuteCommand(context, command);
			} catch (CommandException exception) {
				System.out.println(exception.getOutputText());
			}
		}
	}
}
