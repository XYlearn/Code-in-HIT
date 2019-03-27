package helper.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReaderFileReader implements FileReadStrategy {

	File file;
	BufferedReader reader;
	
	@Override
	public File getFile() {
		return file;
	}

	@Override
	public void setFile(File file) throws IOException {
		if(null != file) {
			close();
		}
		this.file = file;
		this.reader = new BufferedReader(new FileReader(file));
	}

	@Override
	public String readLine() throws IOException {
		return reader.readLine();
	}

	@Override
	public void close() throws IOException {
		if(null != reader)
			reader.close();
		reader = null;
		file = null;
	}

}
