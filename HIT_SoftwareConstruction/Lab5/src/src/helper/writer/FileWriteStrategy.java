package helper.writer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * interface that can write content to file
 */
public interface FileWriteStrategy extends Closeable {
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
	 * write a line to file. '\n' will be automatically appended
	 * 
	 * @param line
	 *            line to write
	 */
	void writeLine(String line) throws IOException;
}
