package helper.reader;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ScannerFileReader implements FileReadStrategy {
	
	private File file;
	private Scanner scanner;

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public void setFile(File file) throws IOException {
		if(this.file != null) {
			close();
		}
		this.file = file;
		
		scanner = new Scanner(file);
	}

	@Override
	public String readLine() throws IOException {
		if(scanner.hasNextLine())
			return scanner.nextLine().replaceAll("\n|\r\n", "");
		else if(scanner.hasNext())
			return scanner.next();
		else {
			return null;
		}
	}

	@Override
	public void close() throws IOException {
		scanner.close();
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}
}
