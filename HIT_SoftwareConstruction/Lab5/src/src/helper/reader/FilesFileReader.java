package helper.reader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FilesFileReader implements FileReadStrategy {
	
	private File file;
	private List<String> lines;
	private int lineNo = 0;

	@Override
	public void close() throws IOException {
		file = null;
		lines = null;
		lineNo = 0;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public void setFile(File file) throws IOException {
		lines = Files.readAllLines(Paths.get(file.getPath()), Charset.forName("UTF-8"));
		this.file = file;
		lineNo = 0;
	}

	@Override
	public String readLine() throws IOException {
		if(lines.size() <= lineNo)
			return null;
		else
			return lines.get(lineNo++);
	}

}
