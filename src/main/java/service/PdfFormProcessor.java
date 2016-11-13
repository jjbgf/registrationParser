package service;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by samuelstein on 13.09.2016.
 */
public class PdfFormProcessor {
    private String folder;

    public PdfFormProcessor(String folder) {
        this.folder = folder;
//        this.pdfFile = new File(folder.getFile());
//
//        if (this.pdfFile != null) {
//            try {
//                this.pdfDocument = PDDocument.load(pdfFile);
//                printDocumentAttributes();
//            } catch (IOException e) {
//                System.err.println(e);
//            }
//        }
    }

    private void printDocumentAttributes(PDDocument pdfDocument) {
//        basic information
        PDDocumentInformation info = pdfDocument.getDocumentInformation();

        //pdf metadata
        PDDocumentCatalog catalog = pdfDocument.getDocumentCatalog();
        PDMetadata metadata = catalog.getMetadata();

//        long kb = 0;
//        try {
//            kb = Files.size(pdfFile.toPath()) / 1024;
//        } catch (IOException e) {
//            System.err.println(e);
//        }
        System.out.println(String.format("Document attributes:%nPages: %d%nPDF-Version: %f%nFilesize: %d kB%n", pdfDocument.getNumberOfPages(), pdfDocument.getVersion()));
    }

//    public String getField(String fieldName) {
//        if (pdfDocument != null) {
//            PDField field = pdfDocument.getDocumentCatalog().getAcroForm().getField(fieldName);
//            return field.getValueAsString();
//        } else {
//            return "";
//        }
//        /*PDCheckbox fullTimeSalary = (PDCheckbox) pdAcroForm.getField("fullTimeSalary");
//        if(fullTimeSalary.isChecked()) {
//            log.debug("The person earns a full-time salary");
//        } else {
//            log.debug("The person does not earn a full-time salary");
//        }*/
//    }

    public void processFields() throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(folder))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    System.out.println(filePath);
//                              if(FilenameUtils.getExtension(f))
                }
            });
        }
    }

    public void listFields(PDDocument pdfDocument) {
        if (pdfDocument != null) {
            PDDocumentCatalog catalog = pdfDocument.getDocumentCatalog();
            PDAcroForm form = catalog.getAcroForm();
            List<PDField> fields = form.getFields();

//            System.out.println(String.format("found %s fields in document: %s%n", fields.size(), pdfFile.getName()));

//            fields.stream().forEach(pdField -> System.out.println(String.format("%s: %s", pdField.getFullyQualifiedName(), StringUtils.defaultIfBlank(pdField.getValueAsString(), "-"))));

            for (PDField field : fields) {
                String value = field.getValueAsString();
                String name = field.getFullyQualifiedName();
                System.out.println(String.format("%s: %s", name, StringUtils.defaultIfBlank(value, "-")));
            }
            close(pdfDocument);
        }
    }

    public void close(PDDocument pdfDocument) {
        if (pdfDocument != null) {
            try {
                pdfDocument.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}
