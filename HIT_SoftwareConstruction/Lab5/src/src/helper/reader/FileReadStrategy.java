package helper.reader;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * interface that can read content from file
 */
public interface FileReadStrategy extends Closeable {

	/**
	 * get File to read or write
	 * 
	 * @return File File needed by writing or reading. return null if the File
	 *         not setted
	 */
	File getFile();

	/**
	 * set File to read or write
	 * 
	 * @param file
	 *            file to set
	 * @param mode
	 *            read write mode of file
	 * @return File to set.
	 * @throws FileNotFoundException
	 */
	void setFile(File file) throws IOException;

	/**
	 * read a line from file, and the \n will not be dropped. if EOF is met, the
	 * left content will be returned
	 * 
	 * @param file
	 *            file to read from
	 * @return return all content in file
	 * @exception IOException
	 *                occurs when read file
	 */
	String readLine() throws IOException;
}
