package main;

import export.CsvExporter;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import service.PdfFormProcessor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import static utils.Constants.FILE_EXTENSION;

/**
 * Created by samuelstein on 13.11.2016.
 */
public class Main {
    private static PdfFormProcessor pdfFormProcessor;
    private static CsvExporter csvExporter;
    private static boolean firstRead = true;

    public static void main(String[] args) {

        // create the command line parser
        CommandLineParser parser = new DefaultParser();

        // create the Options
        Options options = new Options();
        options.addOption("i", "input", true, "input folder with the files to process");
        options.addOption("o", "output", true, "output directory for processed files");
//        options.addOption("v", "verbose", false, "talks about everything");

        // automatically generate the help texts
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar registrationParser", options);

        try {
            CommandLine cmd = parser.parse(options, args);
            String inputFolder = cmd.getOptionValue("i");
            String outputFolder = cmd.getOptionValue("o");
            if (isFolder(inputFolder) && isFolder(outputFolder)) {
                System.out.println(String.format("Processing all files from: %s and outputs it to: %s", inputFolder, outputFolder));
                processPdfDocumentsFromFolder(inputFolder, outputFolder);
            } else {
                System.out.println("No or wrong arguments given. Nothing to work. Bye.");
            }

        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void processPdfDocumentsFromFolder(String inputFolder, String outputFolder) {
        long startTime = System.currentTimeMillis();
        pdfFormProcessor = new PdfFormProcessor();
        csvExporter = new CsvExporter();
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputFolder + File.separator + generateFileName()), "ISO-8859-1"));

            Files.newDirectoryStream(Paths.get(inputFolder),
                    path -> path.toString().endsWith(FILE_EXTENSION))
                    .forEach((path) -> processFile(path, writer));
            writer.flush();
            IOUtils.closeQuietly(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(String.format("%nTime needed for processing: %d ms", System.currentTimeMillis() - startTime));
    }

    private static void processFile(Path path, Writer writer) {
        pdfFormProcessor.loadPdfFile(path);
        pdfFormProcessor.printDocumentAttributes();
        pdfFormProcessor.printFieldsAndValues();
        try {
            if (firstRead) {
                //only read once the form field names as csv headlines
                csvExporter.writeLine(writer, pdfFormProcessor.getFieldNames());
                firstRead = false;
            }
            csvExporter.writeLine(writer, pdfFormProcessor.getFieldValues());
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfFormProcessor.close();
    }

    private static boolean isFolder(String folderPath) {
        if (StringUtils.isNotBlank(folderPath)) {
            if (new File(folderPath).isDirectory()) {
                return true;
            }
        }
        return false;
    }

    private static String generateFileName() {
        return new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + "-export.csv";
    }
}
