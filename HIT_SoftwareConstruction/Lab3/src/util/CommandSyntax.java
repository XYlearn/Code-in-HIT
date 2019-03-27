package util;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Represents the syntax of command
 */
public class CommandSyntax {

	/**
	 * AF : mnem is the keyword(first word) of command. syntax is regular
	 * expressions that specify the concrete syntax of command
	 * 
	 * RI : syntax[0] equals to mnem.
	 */

	private final String mnem;
	private final String[] syntax;

	/**
	 * Constructor
	 * 
	 * @param mnem
	 *            the mnem of command
	 * @param syntax
	 *            syntax of command, use regex to express. for example {"vertex",
	 *            "--add", "\\w+"} matches add vertex with 3 arguments and the thrid
	 *            argument is a string matches \\w+
	 */
	public CommandSyntax(String mnem, String[] syntax) {
		this.mnem = mnem;
		this.syntax = syntax;
	}

	/**
	 * get mnem
	 * 
	 * @return mnem of command
	 */
	public String getMnem() {
		return this.mnem;
	}

	/**
	 * get syntax
	 * 
	 * @return syntax of command
	 */
	public String[] getSyntax() {
		return this.syntax;
	}

	/**
	 * check if a command match the syntax
	 * 
	 * @param command
	 *            command to check
	 * @return return true if the command match the syntax.
	 */
	public boolean match(String command) {
		String[] args = command.trim().split("\\s+");
		// check the args length
		if (args.length != syntax.length)
			return false;

		for (int i = 0; i < syntax.length; i++) {
			String regex = "^" + syntax[i] + "$";
			String arg = args[i];
			if (!Pattern.matches(regex, arg))
				return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		// check the equality by compare mnem and syntax
		if (obj instanceof CommandSyntax) {
			CommandSyntax dst = (CommandSyntax) obj;
			return dst.getMnem().equals(this.getMnem()) && Arrays.deepEquals(dst.getSyntax(), getSyntax());
		} else
			return false;
	}

	@Override
	public int hashCode() {
		int hash = mnem.hashCode() * 5;
		for (String s : syntax) {
			hash += s.hashCode();
		}
		return hash;
	}
}
