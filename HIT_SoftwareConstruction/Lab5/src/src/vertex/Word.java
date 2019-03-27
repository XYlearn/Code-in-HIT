package vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Vertex wrapping String word
 */
public class Word extends Vertex {

	/**
	 * AF : use String label to represent word
	 * 
	 * RI : String is non-empty String with no space
	 * 
	 * safety from re exposure : label is private and final
	 */

	/**
	 * Word Constructor
	 * 
	 * @param label
	 *            word
	 */
	private Word(String label) {
		super(label);
	}

	@Override
	@Deprecated
	public void fillVertexInfo(String[] args) {
		return;
	}

	@Override
	protected void checkRep() {
		// super.checkRep();
		// assert label.split("\\s").length == 1;
	}

	@Override
	public Vertex clone() {
		// return new Word(getLabel());
		return this;
	}

	/**
	 * Generate a word
	 * 
	 * @param word
	 *            content of Word
	 * @return instance of Word
	 */
	public static Word wrap(String word) {
		return new Word(word);
	}

	/**
	 * Generate a Word
	 * 
	 * @param word
	 *            label of word
	 * @param args
	 *            arguments to pass(not used)
	 * @return return generated Word Instance
	 */
	public static Word wrap(String word, String[] args) {
		Word vertex = new Word(word);
		vertex.fillVertexInfo(args);
		vertex.checkRep();
		return vertex;
	}

	@Override
	public List<String> getVertexInfo() {
		return new ArrayList<>();
	}

}
