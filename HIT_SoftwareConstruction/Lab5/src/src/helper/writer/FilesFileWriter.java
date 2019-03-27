package helper.writer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class FilesFileWriter implements FileWriteStrategy {

	private File file;
	private List<String> lines = new ArrayList<>();
	private static int MAX_LINE = 512 * 1024;

	@Override
	public void close() throws IOException {
		if (null != file) {
			flush();
			file = null;
			lines = new ArrayList<>(MAX_LINE);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
	}

	/**
	 * write content cached to file. The Writer mustn't be closed
	 */
	protected void flush() throws IOException {
		Files.write(Paths.get(file.getPath()), lines,
				StandardOpenOption.APPEND);
		lines = new ArrayList<>();
	}

	@Override
	public File getFile() {
		return this.file;
	}

	@Override
	public void setFile(File file) throws IOException {
		close();
		this.file = file;
	}

	@Override
	public void writeLine(String line) throws IOException {
		if(line.length() >= MAX_LINE)
			flush();
		lines.add(line);
	}

}
