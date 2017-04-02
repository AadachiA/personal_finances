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

    private static final String RESOURCES_PATH      = "Finances/";
    private static final String OUTPUT_PATH         = "out/";
    private static final String OUTPUT_FILENAME     = "_summary.txt";
    private static final String COLUMNS_LAYOUT      = "%-11s %-11s\n";
    private static final String COLUMN_A            = "Month";
    private static final String COLUMN_B            = "Net Earning/Spending";
    private static final String DECORATION_LINE     = "===================================";
    private static final String HEADER_COLUMNS      = String.format(COLUMNS_LAYOUT, COLUMN_A, COLUMN_B);
    private static final String HEADER              = HEADER_COLUMNS + DECORATION_LINE;
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

        // 1. Iterates over the content of the directory.
        for(File file: directory.listFiles()) {
            // 1.1. If the element is a file is parsed.
            if (file.isFile())
                parseFile(file);
        }
    }

    /**
     * This method:
     * 1. Parses the content of the file.
     *      1.1 Captures the costData, parses, and summarizes.
     * 2. Submits the results to new files.
     *
     * @param file          The file object to be parsed.
     * @throws IOException  An IOException.
     */
    private static void parseFile(File file) throws IOException {

        Scanner scanner     = new Scanner(file);
        String pattern      = "(\\-|\\+)(\\$\\d+\\.\\d+)";
        float totalCost     = 0;

        // 1. Iterates over all the tokens of the file.
        while(scanner.hasNext()) {
            // 1.1. If the token matches with the pattern: +$123.45, -$67.89
            if(scanner.hasNext(pattern))
                // 1.1.1 The token is parsed and summarized in totalCost.
                totalCost += parseCostData(scanner.next());
            // 1.2. If not matches simply is ignored.
            else
                scanner.next();
        }
        scanner.close();

        // 2. Gathers and prepares all the data that is going to be submitted into the file.
        String filename         = file.getName();
        String targetFilename   = prepareTargetFilename(filename);
        String columnA          = prepareColumnA(filename);
        String columnB          = prepareColumnB(totalCost);

        // 3. Submits the data into the file.
        submitToFile(targetFilename, columnA, columnB);
    }

    /**
     * This method parses the cost data token, and converts it into a float.
     *
     * @param costData      The cost data.
     * @return              The cost data as float.
     */
    private static float parseCostData(String costData) {

        // 1. Gets the sign of the token. ("-")
        char sign   = costData.charAt(0);
        // 2. Parses the token into a float ignoring the first two positions. ("-$")
        float cost  = Float.parseFloat(costData.substring(2, costData.length()));
        // 3. Adds the sign again.
        return (sign == '+')? +cost: -cost;
    }

    /**
     * This method prepares the target filename.
     * E.g. yyyy_summary.txt
     *
     * @param filename  The filename of the current file.
     * @return          The final target filename.
     */
    private static String prepareTargetFilename(String filename) {

        // 1. Gets the position of the dash and the point
        // in order to extract the year of the filename.
        int dashSign    = filename.indexOf('-');
        int pointSign   = filename.indexOf('.');
        String year     = filename.substring(dashSign + 1, pointSign);

        // 2. Returns the new filename.
        return year + OUTPUT_FILENAME;
    }

    /**
     * This method prepares how the data on column A must look like.
     * E.g. November
     *
     * @param filename  The filename of the current file.
     * @return          The final column A data.
     */
    private static String prepareColumnA(String filename) {

        // 1. Gets the position of the dash
        // in order to extract the month index of the filename.
        int dashSign    = filename.indexOf('-');
        int monthIndex  = Integer.parseInt(filename.substring(0, dashSign));

        // 2. Returns the name of the month.
        return MONTHS[monthIndex - 1];
    }

    /**
     * This method prepares how the data on column B must look like.
     * E.g. +$34.69
     *
     * @param totalCost The raw data.
     * @return          The final column B data.
     */
    private static String prepareColumnB(float totalCost) {

        // 1. If the number is positive.
        if(totalCost >= 0)
            // 1.1. Returns the number with format +$123.45
            return "+$" + String.format("%.2f", totalCost);
        // 2. If the number is negative.
        else
            // 2.1. Returns the number with format -$123.45. (-1 * -totalCost = totalCost)
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

        // 1. Checks if the directory exists, if not it's created.
        createDirectory(OUTPUT_PATH);
        // 2. Checks if the file exists, if not it's created.
        createFile(OUTPUT_PATH, filename);
        // 3. Generates the complete path, and formats the line to be written.
        String path     = OUTPUT_PATH + filename;
        String string   = String.format(COLUMNS_LAYOUT, columnA, ColumnB);
        // 4. Writes on file.
        writeOnFile(path, string);
    }

    /**
     * This method creates physically the directory where the file will be allocated.
     *
     * @param path  The directory path (path + directory name).
     * @return      The object file.
     */
    private static File createDirectory(String path) {

        // 1. Creates an instance of the directory reference.
        File directory = new File(path);

        // 2. If the directory doesn't exists, or is not a directory.
        if(!directory.exists() || !directory.isDirectory()) {
            // 2.1. Creates the directory, but if it fails, prints an error and exits the program.
            if(!directory.mkdir()) {
                System.out.println("Error: The directory couldn't be created");
                System.exit(1);
            }
        }

        // 3. Returns the reference.
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

        // 1. Creates an instance of the file reference.
        File file = new File(path + filename);

        // 2. If the file doesn't exists, or is not a file.
        if(!file.exists() || !file.isFile()) {
            // 2.1. Creates the file, but if it fails, prints an error and exits the program.
            if(!file.createNewFile()) {
                System.out.println("Error: The file couldn't be created");
                System.exit(1);
            }
            // 2.2. If the file could be created a header is added.
            else
                writeOnFile(path + filename, HEADER);
        }

        // 3. Returns the reference.
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

        // 1. Creates an instance of the file reference.
        File file               = new File(path);
        // 2. Gets all the previous content allocated on it.
        String fileContent      = getFileContent(file);
        // 3. Opens the file to write on it. This action erases the content.
        PrintWriter printWriter = new PrintWriter(file);
        // 4. Writes the previous content and then the new one.
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

        Scanner scanner     = new Scanner(file);
        String prevContent  = "";

        // 1. Iterates over all the lines of the file.
        while(scanner.hasNextLine()) {
            // 1.1. Stores every line on prevContent.
            prevContent += scanner.nextLine() + "\n";
        }

        scanner.close();
        return prevContent;
    }
}