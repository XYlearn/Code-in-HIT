package helper.writer;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriterFileWriter implements FileWriteStrategy, Closeable {
	
	private File file;
	private BufferedWriter writer;

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public void setFile(File file) throws IOException {
		this.close();
		this.file = file;
		this.writer = new BufferedWriter(new FileWriter(file));
	}

	@Override
	public void writeLine(String line) throws IOException {
		writer.write(line+'\n');
	}

	@Override
	public void close() throws IOException {
		if(null != writer)
			writer.close();
		writer = null;
		this.file = null;
	}

}
