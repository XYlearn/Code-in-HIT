package helper.reader;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public abstract class FileReadStrategyTest {

	protected File file;
	protected FileReadStrategy reader;
	
	@Before
	public void setUp() throws Exception {
		file = new File("test/src/helper/testcase.txt");
	}

	@Test
	public void testReadLine() throws IOException {
		reader.setFile(file);
		assertEquals(reader.readLine(), "GraphType = NetworkTopology");
		assertEquals(reader.readLine(), "GraphName = LabNetwork");
		for(int i = 0; i < 8; i++)
			reader.readLine();
		assertTrue(reader.readLine() == null);
		reader.close();
	}

}
