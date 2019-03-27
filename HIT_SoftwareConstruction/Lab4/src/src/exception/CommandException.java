package exception;

/**
 * Exception raised in command parsing, executing or graph build commands
 */
public class CommandException extends Exception {

	private static final long serialVersionUID = 1L;

	private int lineNo = 0;
	private int startPos = 0;
	private String command = "";
	private boolean ignorable = false;

	/**
	 * Get the line no where exception occurs
	 * 
	 * @return line number exception occurs
	 */
	public int getLineNo() {
		return lineNo;
	}

	/**
	 * Get the start position where exception raises
	 * 
	 * @return the start position where exception raises
	 */
	public int getStartPos() {
		return startPos;
	}

	/**
	 * Get command that causes Exception
	 * 
	 * @return command that causes Exception
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Set line no
	 * 
	 * @param lineNo
	 *            line no where exception occurs
	 */
	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}

	/**
	 * Set the start position where exception raises
	 * 
	 * @param the
	 *            start position where exception raises
	 */
	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	/**
	 * Set command that causes Exception
	 * 
	 * @param command
	 *            command that causes Exception
	 */
	public void setCommand(String command) {
		this.command = command;
	}
	
	/**
	 * check if the exception can be ignored
	 * @return return true if the exception can be ignored
	 */
	public boolean isIgnorable() {
		return this.ignorable;
	}
	
	/**
	 * set ignorable attribute of exception
	 * @param ignorable the ignorable attribute to set
	 */
	public void setIgnorable(boolean ignorable) {
		this.ignorable = ignorable;
	}

	public CommandException(String msg) {
		super(msg);
	}
	
	public String getLogText() {
		if (isIgnorable()) {
			String text = getMessage() + "\nWarning at " + "pos:"+ getStartPos()
					+ " : " + getCommand();
			return text;
		} else {
			String text = getMessage() + "\nException at " + "pos:" + getStartPos()
					+ " : " + getCommand();
			return text;
		}
	}
	
	public String getOutputText() {
		if (isIgnorable()) {
			String text = getMessage() + "\nWarning at " + getLineNo() + ":"+ getStartPos()
					+ " : " + getCommand();
			return text;
		} else {
			String text = getMessage() + "\nException at " + getLineNo() + ":" + getStartPos()
					+ " : " + getCommand();
			return text;
		}
	}
}
