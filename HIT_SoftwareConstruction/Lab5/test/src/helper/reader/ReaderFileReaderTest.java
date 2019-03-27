package helper.reader;

import org.junit.Before;

public class ReaderFileReaderTest extends FileReadStrategyTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
		reader = new ReaderFileReader();
	}

}
