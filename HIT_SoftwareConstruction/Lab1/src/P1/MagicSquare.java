import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MagicSquare {

	/**
	 * check if matrix in the given file is a magic square
	 * 
	 * @param fileName
	 *            name of file where the matrix stored
	 * @return true if matrix is legal magic square
	 */
	public static boolean isLegalMagicSquare(String fileName) {
		int order = 0;
		int[][] matrix = null;
		ArrayList<String> lines = new ArrayList<String>();
		BufferedReader bufferedReader = null;

		// Read the matrix and check if the matrix is a square
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			String tempLine;
			while ((tempLine = bufferedReader.readLine()) != null) {
				lines.add(tempLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			if (null != bufferedReader)
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		// try to generate matrix
		order = 0;
		for (String line : lines) {
			if (line.trim() != "")
				order++;
		}
		matrix = new int[order][order];
		int tempRow = 0;
		for (String line : lines) {
			if (line == null) {
				break;
			}
			// split string
			String[] digitStrings = line.split("\t");
			ArrayList<String> digitsList = new ArrayList<>();
			
			// trim and check
			for(int i = 0; i < digitStrings.length; i++) {
				String trimedString = digitStrings[i].trim();
				if(trimedString.length() != 0) {
					digitsList.add(trimedString);					
				}
			}
			
			int cols = digitsList.size();
			// check if the column fits the order
			if (cols != order) {
				System.out.println("[-] not a square matrix");
				return false;
			}
			for (int tempCol = 0; tempCol < cols; tempCol++) {
				try {
					int digit = Integer.parseInt(digitStrings[tempCol]);
					// the digits must be positive
					if (digit <= 0) {
						System.out.println("[-] zero or negative number detected");
						return false;
					}
					matrix[tempRow][tempCol] = digit;
				} catch (NumberFormatException e) {
					System.out.println("[-] illegal format");
					return false;
				}
			}
			tempRow += 1;
		}

		// check if the row number fits the order
		if (tempRow != order) {
			System.out.println("[-] row number conflicts with column number");
			return false;
		}

		return isMagicMatrix(matrix, order);
	}

	private static boolean isMagicMatrix(int[][] matrix, int order) {
		// calculate sum of the first row
		int rowSum = 0;
		for (int i = 0; i < order; i++) {
			rowSum += matrix[0][i];
		}
		// check the sum of each row
		for (int i = 1; i < order; i++) {
			int currRowSum = 0;
			for (int j = 0; j < order; j++) {
				currRowSum += matrix[i][j];
			}
			if (currRowSum != rowSum) {
				return false;
			}
		}

		// check the sum of each column
		for (int i = 0; i < order; i++) {
			int colSum = 0;
			for (int j = 0; j < order; j++) {
				colSum += matrix[j][i];
			}
			if (colSum != rowSum) {
				return false;
			}
		}
		// check the sum of both diagonals
		int diagSum = 0;
		for (int i = 0; i < order; i++) {
			diagSum += matrix[i][i];
		}
		if (diagSum != rowSum) {
			return false;
		}
		diagSum = 0;
		for (int i = 0; i < order; i++) {
			diagSum += matrix[i][order - (i + 1)];
		}
		if (diagSum != rowSum) {
			return false;
		}

		// if all check passed the matrix is a magicSquare
		return true;
	}

	/**
	 * 
	 * @param n
	 *            order of matrix, must be positive odd integer
	 * @return if generate successfully
	 */
	public static boolean generateMagicSquare(int n) {
		// n must be odd number
		if ((n & 1) == 0 || n <= 0) {
			System.out.println("[-] positive odd number is required");
			return false;
		}
		int magic[][] = new int[n][n];
		int row = 0, col = n / 2, i, j, square = n * n;
		for (i = 1; i <= square; i++) {
			magic[row][col] = i;
			if (i % n == 0)
				row++;
			else {
				if (row == 0)
					row = n - 1;
				else
					row--;
				if (col == (n - 1))
					col = 0;
				else
					col++;
			}
		}
		// build content
		StringBuilder stringBuilder = new StringBuilder();
		for (i = 0; i < n; i++) {
			for (j = 0; j < n; j++) {
				stringBuilder.append(String.valueOf(magic[i][j]) + "\t");
			}
			stringBuilder.append("\n");
		}
		String content = stringBuilder.toString();
		
		// print matrix
		System.out.print(content);
		String filename = "6.txt";
		File outFile = new File(path + filename);
		FileOutputStream fout = null;
		BufferedWriter bufferedWriter = null;
		if (!outFile.exists()) {
			try {
				if (!outFile.createNewFile()) {
					System.out.println("[-] Can't Create File");
					System.exit(0);
				}
			} catch (IOException e) {
				System.out.println("[-] Can't Create File");
				System.exit(0);
			}
		}
		try {
			fout = new FileOutputStream(outFile);
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(fout));
			bufferedWriter.write(stringBuilder.toString());
		} catch (IOException e) {
			System.out.println("[-] Generate file error");
		} finally {
			if (null != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != bufferedWriter) {
				try {
					bufferedWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	// test function
	public static void main(String[] args) {
		// if no argument passed, test all txt
		if (args.length < 1) {
			for (int i = 1; i <= 5; i++) {
				String filename = i + ".txt";
				if (isLegalMagicSquare(path + filename)) {
					System.out.println(filename + " is a legal MagicSquare");
				} else {
					System.out.println(filename + " isn't a legal MagicSquare");
				}
			}
		} else {
			String filename = args[0];
			if (isLegalMagicSquare(path + filename)) {
				System.out.println("[+] " + filename + " is a legal MagicSquare");
			} else {
				System.exit(0);
			}
		}

		generateMagicSquare(3);
		generateMagicSquare(4);
		generateMagicSquare(5);
	}

	final static String path = "txt\\";

}
