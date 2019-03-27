package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParseUtil {
	/**
	 * get words in order from content
	 * 
	 * @param content
	 *            content of words in order
	 * @return list of words
	 */
	public static List<String> getWords(String content) {
		List<String> wordList = new ArrayList<>();
		String[] words = content.split("\\s");
		// get words from each line
		for (String word : words) {
			if (word.isEmpty())
				continue;
			else
				wordList.add(word);
		}
		return wordList;
	}

	/**
	 * get words from file
	 * 
	 * @param pathname
	 *            pathname of file to read from
	 * @return List of words in order
	 * @throws IOException
	 *             throw if file not exists or can't be read
	 */
	public static List<String> getWordsFromFile(String pathname) throws IOException {
		// read content from file
		File file = new File(pathname);
		FileReader fileReader = new FileReader(file);
		char[] buf = new char[(int) file.length()];
		fileReader.read(buf);
		fileReader.close();
		// call getWords
		return getWords(new String(buf));
	}

	/**
	 * convert words in words list to lower case
	 * 
	 * @param words
	 *            new word list after conversion
	 * @return
	 */
	public static List<String> wordsToLowercase(List<String> words) {
		List<String> lwords = new ArrayList<>();

		for (String word : words)
			lwords.add(word.toLowerCase());

		return lwords;
	}

	/**
	 * get the first non-space word of s
	 * 
	 * @param s
	 *            string to get word
	 * @return the first word of s. if s has no non-space word, empty string will be
	 *         returned
	 */
	public static String firstWord(String s) {
		return s.trim().split("\\s+")[0];
	}

	/**
	 * Read content from file
	 * 
	 * @param file
	 *            file to read content from
	 * @return return all content of fie
	 * @exception IOException
	 *                throw when the file can't be read
	 */
	public static String readFromFile(File file) throws IOException {
		char[] cbuf = new char[(int) file.length()];
		BufferedReader reader = new BufferedReader(new FileReader(file));
		reader.read(cbuf);
		reader.close();

		return String.valueOf(cbuf);
	}
}
