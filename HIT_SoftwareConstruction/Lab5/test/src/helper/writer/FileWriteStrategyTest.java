package helper.writer;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import helper.reader.FileReadStrategy;
import helper.reader.FilesFileReader;

public abstract class FileWriteStrategyTest {
	
	protected FileWriteStrategy writer;
	protected File file;

	@Before
	public void setUp() throws Exception {
		file = new File("rsrc/tmp.txt");
		if(file.exists())
			file.delete();
		file.createNewFile();
	}

	@Test
	public void testWriteLine() throws Exception {
		writer.setFile(file);
		writer.writeLine("line1");
		writer.writeLine("line2");
		writer.close();
		FileReadStrategy reader = new FilesFileReader();
		reader.setFile(file);
		assertEquals(reader.readLine(), "line1");
		assertEquals(reader.readLine(), "line2");
		assertTrue(reader.readLine() == null);
		reader.close();
	}

}
