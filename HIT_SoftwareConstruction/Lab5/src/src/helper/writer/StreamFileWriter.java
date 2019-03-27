package helper.writer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class StreamFileWriter implements FileWriteStrategy {

	private File file;
	private BufferedOutputStream stream;
	
	@Override
	public void close() throws IOException {
		if(null != stream)
			stream.close();
		file = null;
		stream = null;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public void setFile(File file) throws IOException {
		this.close();
		this.stream = new BufferedOutputStream(new FileOutputStream(file));
		this.file = file;
	}

	@Override
	public void writeLine(String line) throws IOException {
		stream.write(line.getBytes());
		stream.write('\n');
	}

}
