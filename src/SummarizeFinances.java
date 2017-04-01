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
 * @since   2017-03-31
 */
public class SummarizeFinances{

	private static final String RESOURCES_PATH 		= "resources/"; // TODO: Change to finances
	private static final String OUTPUT_PATH 		= "outtest/"; // TODO: Change to final path
	private static final String FILENAME 	 		= "_summary.txt";
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
     *
     * @param path
     */
	private static void readDirectory(String path) throws IOException {

        File directory = new File(path);

        for(File file: directory.listFiles()) {
            if (file.isFile())
                parseFile(file); // TODO: Try to fix the order
        }
    }

    /**
     * Helper
     * @param file
     * @throws IOException An IOException.
     */
    private static void parseFile(File file) throws IOException {

    	// TODO: Refactor
        String filename     = file.getName();
        int dashSign        = filename.indexOf('-');
        int pointSign       = filename.indexOf('.');
        int month           = Integer.parseInt(filename.substring(0, dashSign));
        int year            = Integer.parseInt(filename.substring(dashSign + 1, pointSign));
        float total_cost    = 0;

        Scanner scanner     = new Scanner(file);
        String pattern  = "(\\-|\\+)(\\$\\d+\\.\\d+)";

        while(scanner.hasNext()) {
            if(scanner.hasNext(pattern))
                total_cost += translateCostColumn(scanner.next());
            else
                scanner.next();
        }

        // TODO: EXternal method
        String filename2 = year + FILENAME;
        String columnA 	= MONTHS[month-1];
        String columnB 	= "" + total_cost;

        System.out.printf("%s %s %s\n", filename2, columnA, columnB);
        submitToFile(filename2, columnA, columnB);
    }

    /**
     * Helper
     * @param data
     * @return
     * @throws IOException
     */
    private static float translateCostColumn(String data) throws IOException {

        char sign   = data.charAt(0);
        float cost  = Float.parseFloat(data.substring(2, data.length()));

        return (sign == '+')? +cost: -cost;
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
	 * This method creates physically the file.
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