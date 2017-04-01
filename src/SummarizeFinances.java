import java.util.Scanner;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;

public class SummarizeFinances{

	private static final String RESOURCES_PATH 		= "resources/"; // TODO: Change to finances
	private static final String OUTPUT_PATH 		= "out/";
	private static final String FILENAME 	 		= "_summary.txt";
	private static final String COLUMNS_LAYOUT 		= "%-11s %-11s\n";
	private static final String COLUMN_A 			= "Month";
	private static final String COLUMN_B 			= "Net Earning/Spending";
	private static final String DECORATION_LINE 	= "===================================";
	private static final String HEADER_COLUMNS		= String.format(COLUMNS_LAYOUT, COLUMN_A, COLUMN_B);
	private static final String HEADER 				= HEADER_COLUMNS + DECORATION_LINE;

	/**
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		String filename = 2013 + FILENAME;
		String columnA 	= "columnA";
		String columnB 	= "columnB";

		submitToFile(filename, columnA, columnB);
	}

	/**
	 *
	 * @param filename
	 * @param columnA
	 * @param ColumnB
	 * @throws IOException
	 */
	private static void submitToFile(String filename, String columnA, String ColumnB) throws IOException {

		File directory = createDirectory(OUTPUT_PATH);

		if(directory.exists()) {

			String pathname = OUTPUT_PATH + filename;
			File file 		= createFile(pathname);

			if(file.exists())
				writeOnFile(pathname, String.format(COLUMNS_LAYOUT, columnA, ColumnB));
		}
	}

	/**
	 *
	 * @param pathname
	 * @return
	 */
	private static File createDirectory(String pathname) {

		File directory = new File(pathname);

		if(!directory.exists() || !directory.isDirectory()) {
			if(!directory.mkdir()) {
				System.out.println("Error: The directory couldn't be created");
				System.exit(1);
			}
		}

		return directory;
	}

	/**
	 *
	 * @param pathname
	 * @return
	 * @throws IOException
	 */
	private static File createFile(String pathname) throws IOException {

		File file = new File(pathname);

		if(!file.exists()) {
			if(!file.createNewFile()) {
				System.out.println("Error: The file couldn't be created");
				System.exit(1);
			}
			else
				writeOnFile(pathname, HEADER);
		}

		return file;
	}

	/**
	 *
	 * @param pathname
	 * @param string
	 * @throws IOException
	 */
	private static void writeOnFile(String pathname, String string) throws IOException {

		File file 			= new File(pathname);
		Scanner scanner 	= new Scanner(file);
		String prevContent 	= "";

		while(scanner.hasNextLine()) {
			prevContent += scanner.nextLine() + "\n";
		}

		PrintWriter printWriter = new PrintWriter(file);
		printWriter.print(prevContent);
		printWriter.print(string);
		printWriter.close();
		scanner.close();
	}
}