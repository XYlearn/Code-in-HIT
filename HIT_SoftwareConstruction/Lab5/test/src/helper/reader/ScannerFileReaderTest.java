package helper.reader;

import org.junit.Before;

public class ScannerFileReaderTest extends FileReadStrategyTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
		reader = new ScannerFileReader();
	}

}
