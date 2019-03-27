package helper.reader;

import org.junit.Before;

public class FilesFileReaderTest extends FileReadStrategyTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
		reader = new FilesFileReader();
	}

}
