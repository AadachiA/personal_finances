import java.util.Scanner;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;

/**
 * Personal Finances
 * File processing program, to summarize personal finances.
 * The program read from monthly transaction files, and create a summary file for each year.
 * Each summary file lists the year, and the net financial gain (or loss) for each month in that year.
 *
 * @author  Ayaka Adachi [ID:100225327, Sec:005]
 * @author  Sergio Berlinches [ID:100291288, Sec:005]
 * @since   2017-04-01
 */
public class SummarizeFinances{

	private static final String RESOURCES_PATH 		= "Finances/";
	private static final String OUTPUT_PATH 		= "out/";
	private static final String OUTPUT_FILENAME 	= "_summary.txt";
	private static final String COLUMNS_LAYOUT 		= "%-11s %-11s\n";
	private static final String COLUMN_A 			= "Month";
	private static final String COLUMN_B 			= "Net Earning/Spending";
	private static final String DECORATION_LINE 	= "===================================";
	private static final String HEADER_COLUMNS		= String.format(COLUMNS_LAYOUT, COLUMN_A, COLUMN_B);
	private static final String HEADER 				= HEADER_COLUMNS + DECORATION_LINE;
	private static final String[] MONTHS            = {"January", "February", "March", "April", "May", "June", "July",
                                                        "August", "September", "October", "November", "December"};

	/**
	 * @param args          The program arguments.
	 * @throws IOException  An IOException.
	 */
	public static void main(String[] args) throws IOException {
		readDirectory(RESOURCES_PATH);
	}

    /**
     * This method:
	 * 1. Reads all the files inside a directory.
	 * 2. Parses the content of each file.
	 *
     * @param path The directory path.
     */
	private static void readDirectory(String path) throws IOException {

        File directory = new File(path);

        for(File file: directory.listFiles()) {
            if (file.isFile())
                parseFile(file);
        }
    }

    /**
     * This method:
	 * 1. Parses the content of the file.
	 * 		1.1 Captures the costData, parses, and summarizes.
	 * 2. Submits the results to new files.
	 *
     * @param file 			The file object to be parsed.
     * @throws IOException 	An IOException.
     */
    private static void parseFile(File file) throws IOException {

        Scanner scanner     = new Scanner(file);
        String pattern  	= "(\\-|\\+)(\\$\\d+\\.\\d+)";
		float totalCost 	= 0;

        while(scanner.hasNext()) {
            if(scanner.hasNext(pattern))
                totalCost += parseCostData(scanner.next());
            else
                scanner.next();
        }
        scanner.close();

        String filename 		= file.getName();
		String targetFilename 	= prepareTargetFilename(filename);
        String columnA 			= prepareColumnA(filename);
        String columnB 			= prepareColumnB(totalCost);

        submitToFile(targetFilename, columnA, columnB);
    }

	/**
	 * This method parses the cost data token, and converts it into a float.
	 *
	 * @param costData 		The cost data.
	 * @return				The cost data as float.
	 */
	private static float parseCostData(String costData) {

		char sign   = costData.charAt(0);
		float cost  = Float.parseFloat(costData.substring(2, costData.length()));

		return (sign == '+')? +cost: -cost;
	}

	/**
	 * This method prepares the target filename.
	 * E.g. yyyy_summary.txt
	 *
	 * @param filename 	The filename of the current file.
	 * @return 			The final target filename.
	 */
	private static String prepareTargetFilename(String filename) {

		int dashSign 	= filename.indexOf('-');
		int pointSign   = filename.indexOf('.');
		String year     = filename.substring(dashSign + 1, pointSign);

		return year + OUTPUT_FILENAME;
	}

	/**
	 * This method prepares how the data on column A must look like.
	 * E.g. November
	 *
	 * @param filename 	The filename of the current file.
	 * @return 			The final column A data.
	 */
    private static String prepareColumnA(String filename) {

		int dashSign 	= filename.indexOf('-');
		int monthIndex  = Integer.parseInt(filename.substring(0, dashSign));

		return MONTHS[monthIndex - 1];
	}

	/**
	 * This method prepares how the data on column B must look like.
	 * E.g. +$34.69
	 *
	 * @param totalCost The raw data.
	 * @return 			The final column B data.
	 */
	private static String prepareColumnB(float totalCost) {
		if(totalCost > 0)
			return "+$" + String.format("%.2f", totalCost);
		else
			return "-$" + String.format("%.2f", -totalCost);
	}

	/**
	 * This method submits the text inside the specified file.
     * If the file doesn't exist, It's created.
     * If the directory doesn't exists, It's created.
     *
	 * @param filename      The name of the file.
	 * @param columnA       The text to be inserted on the left column
	 * @param ColumnB       The text to be inserted on the right column
	 * @throws IOException  An IOException.
	 */
	private static void submitToFile(String filename, String columnA, String ColumnB) throws IOException {

		File directory = createDirectory(OUTPUT_PATH);

		if(directory.exists()) {

		    String path     = OUTPUT_PATH + filename;
		    String string   = String.format(COLUMNS_LAYOUT, columnA, ColumnB);
			File file       = createFile(OUTPUT_PATH, filename);

			if(file.exists())
				writeOnFile(path, string);
		}
	}

	/**
	 * This method creates physically the directory where the file will be allocated.
     *
	 * @param path  The directory path (path + directory name).
	 * @return      The object file.
	 */
	private static File createDirectory(String path) {

		File directory = new File(path);

		if(!directory.exists() || !directory.isDirectory()) {
			if(!directory.mkdir()) {
				System.out.println("Error: The directory couldn't be created");
				System.exit(1);
			}
		}

		return directory;
	}

	/**
	 * This method creates physically the file, and writes the header on it.
     *
	 * @param path          The destination path (path + directory name).
     * @param filename      The filename.
	 * @return              The object file.
	 * @throws IOException  An IOException.
	 */
	private static File createFile(String path, String filename) throws IOException {

		File file = new File(path + filename);

		if(!file.exists()) {
			if(!file.createNewFile()) {
				System.out.println("Error: The file couldn't be created");
				System.exit(1);
			}
			else
				writeOnFile(path + filename, HEADER);
		}

		return file;
	}

	/**
	 * This method writes at the end of the file.
     *
	 * @param path          The path where the file is allocated (path + filename).
	 * @param string        The text to be added.
	 * @throws IOException  An IOException.
	 */
	private static void writeOnFile(String path, String string) throws IOException {

		File file 			    = new File(path);
        String fileContent      = getFileContent(file);
		PrintWriter printWriter = new PrintWriter(file);

		printWriter.print(fileContent);
		printWriter.print(string);
		printWriter.close();
	}

    /**
     * This method scans and returns all the content of a file.
     *
     * @param file          The file to be scanned.
     * @return              The content of the file.
     * @throws IOException  An IOException.
     */
	private static String getFileContent(File file) throws IOException {

        Scanner scanner 	= new Scanner(file);
        String prevContent 	= "";

        while(scanner.hasNextLine()) {
            prevContent += scanner.nextLine() + "\n";
        }

        scanner.close();
        return prevContent;
    }
}