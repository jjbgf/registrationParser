import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import service.PdfFormProcessor;

/**
 * Created by samuelstein on 13.11.2016.
 */
public class Main {

    public static void main(String[] args) {

        // create the command line parser
        CommandLineParser parser = new DefaultParser();

        // create the Options
        Options options = new Options();
        options.addOption("i", "input", true, "do not hide entries starting with .");
        options.addOption("o", "output", true, "do not list implied . and ..");
        options.addOption("v", "verbose", false, "talks about everything");

        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("registrationParser", options );

        try {
            CommandLine cmd = parser.parse(options, args);
            String inputFolder = cmd.getOptionValue("i");
            String outputFolder = cmd.getOptionValue("o");
            if (StringUtils.isNotBlank(inputFolder) && StringUtils.isNotBlank(outputFolder)) {
                System.out.println(String.format("Processing all files from %s and outputs it to %s", inputFolder, outputFolder));
                readPdfDocumentsFromFolder(inputFolder);
            } else {
                System.out.println("No arguments nothing to work. bye.");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void readPdfDocumentsFromFolder(String folder) {
        long startTime = System.currentTimeMillis();

        PdfFormProcessor pdfFormProcessor = new PdfFormProcessor(folder);
//        pdfFormProcessor.listFields();

        System.out.println(String.format("%nTime needed for processing: %d ms", System.currentTimeMillis() - startTime));

//        pdfFormProcessor.close();
    }
}
