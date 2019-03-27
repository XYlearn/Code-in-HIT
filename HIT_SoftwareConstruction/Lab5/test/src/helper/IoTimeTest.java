package helper;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import edge.Edge;
import exception.CommandException;
import graph.Graph;
import helper.reader.FileReadStrategy;
import helper.reader.FilesFileReader;
import helper.reader.ReaderFileReader;
import helper.reader.ScannerFileReader;
import helper.writer.FileWriteStrategy;
import helper.writer.FilesFileWriter;
import helper.writer.StreamFileWriter;
import helper.writer.WriterFileWriter;
import util.ParseUtil;
import vertex.Vertex;

public class IoTimeTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws IOException, CommandException {
		Graph<Vertex, Edge> graph;

		graph = testInputTime("rsrc/file1.txt", new FilesFileReader());
		graph = testInputTime("rsrc/file1.txt", new ReaderFileReader());
		graph = testInputTime("rsrc/file1.txt", new ScannerFileReader());
		testOutputTime("rsrc/file1.txt", "rsrc/tmp.txt", graph, new FilesFileWriter());
		testOutputTime("rsrc/file1.txt", "rsrc/tmp.txt", graph, new WriterFileWriter());
		testOutputTime("rsrc/file1.txt", "rsrc/tmp.txt", graph, new StreamFileWriter());
		
		graph = testInputTime("rsrc/file2.txt", new FilesFileReader());
		graph = testInputTime("rsrc/file2.txt", new ReaderFileReader());
		graph = testInputTime("rsrc/file2.txt", new ScannerFileReader());
		testOutputTime("rsrc/file2.txt", "rsrc/tmp.txt", graph, new FilesFileWriter());
		testOutputTime("rsrc/file2.txt", "rsrc/tmp.txt", graph, new WriterFileWriter());
		testOutputTime("rsrc/file2.txt", "rsrc/tmp.txt", graph, new StreamFileWriter());
		
		graph = testInputTime("rsrc/file3.txt", new FilesFileReader());
		graph = testInputTime("rsrc/file3.txt", new ReaderFileReader());
		graph = testInputTime("rsrc/file3.txt", new ScannerFileReader());
		testOutputTime("rsrc/file3.txt", "rsrc/tmp.txt", graph, new FilesFileWriter());
		testOutputTime("rsrc/file3.txt", "rsrc/tmp.txt", graph, new WriterFileWriter());
		testOutputTime("rsrc/file3.txt", "rsrc/tmp.txt", graph, new StreamFileWriter());
		
		graph = testInputTime("rsrc/file4.txt", new FilesFileReader());
		graph = testInputTime("rsrc/file4.txt", new ReaderFileReader());
		graph = testInputTime("rsrc/file4.txt", new ScannerFileReader());
		testOutputTime("rsrc/file4.txt", "rsrc/tmp.txt", graph, new FilesFileWriter());
		testOutputTime("rsrc/file4.txt", "rsrc/tmp.txt", graph, new WriterFileWriter());
		testOutputTime("rsrc/file4.txt", "rsrc/tmp.txt", graph, new StreamFileWriter());
	}

	public Graph<Vertex, Edge> testInputTime(String pathname,
			FileReadStrategy reader) throws IOException, CommandException {
		long startTime, endTime, duration;
		Graph<Vertex, Edge> graph;

		startTime = System.currentTimeMillis();
		ParseCommandHelper.setReader(reader);
		graph = ParseCommandHelper.buildGraphFromFile(pathname);
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;
		System.out.println("Input [Reader:" + ParseUtil.getClassName(reader.getClass()) + "] [pathname:"
				+ pathname + "] [duration:" + String.valueOf(duration) + "]");
		System.out.flush();
		return graph;
	}

	public void testOutputTime(String srcPathName, String dstPathName,
			Graph<Vertex, Edge> graph, FileWriteStrategy writer)
			throws IOException {
		long startTime, endTime, duration;

		File file = new File(dstPathName);
		if(file.exists())
			file.delete();
		file.createNewFile();
		
		startTime = System.currentTimeMillis();
		GraphStoreHelper.setWriter(writer);
		GraphStoreHelper.store(graph, file);
		endTime = System.currentTimeMillis();
		duration = endTime - startTime;
		System.out.println("Output [Writer:"
				+ ParseUtil.getClassName(writer.getClass()) + "] [pathname: "
				+ srcPathName + "] [duration" + String.valueOf(duration) + "]");
		System.out.flush();
	}
}
