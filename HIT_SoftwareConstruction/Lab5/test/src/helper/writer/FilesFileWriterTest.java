package helper.writer;

import org.junit.Before;

public class FilesFileWriterTest extends FileWriteStrategyTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
		writer = new FilesFileWriter();
	}

}
