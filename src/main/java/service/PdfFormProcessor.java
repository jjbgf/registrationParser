package service;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by samuelstein on 13.09.2016.
 */
public class PdfFormProcessor {
    private PDDocument pdfDocument;
    private File pdfFile;
    private List<PDField> fields;

    public PdfFormProcessor() {
        this.fields = new ArrayList<>();
    }

    public void loadPdfFile(Path filePath) {
        this.pdfFile = filePath.toFile();

        if (this.pdfFile != null && this.pdfFile.isFile()) {
            try {
                this.pdfDocument = PDDocument.load(pdfFile);
                loadFields();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void printDocumentAttributes() {
//        basic information
        PDDocumentInformation info = pdfDocument.getDocumentInformation();

        long kb = 0;
        try {
            kb = Files.size(pdfFile.toPath()) / 1024;
        } catch (IOException e) {
            System.err.println(e);
        }
        System.out.println(String.format("Document info: %nTitle: %s%nAuthor: %s%n", info.getTitle(), info.getAuthor()));
        System.out.println(String.format("Document attributes:%nPages: %d%nPDF-Version: %f%nFilesize: %d kB%n", pdfDocument.getNumberOfPages(), pdfDocument.getVersion(), kb));
    }

    public PDMetadata getMetadataFromDocument() {
        if (this.pdfDocument != null && pdfDocument.getDocumentCatalog() != null) {
            return pdfDocument.getDocumentCatalog().getMetadata();
        }
        return null;
    }

    public String getField(String fieldName) {
        Optional<PDField> first = fields.stream().filter(pdField -> StringUtils.equalsIgnoreCase(pdField.getFullyQualifiedName(), fieldName)).findFirst();

        if (first.isPresent()) {
            return first.get().getValueAsString();
        } else {
            return "";
        }
        /*PDCheckbox fullTimeSalary = (PDCheckbox) pdAcroForm.getField("fullTimeSalary");
        if(fullTimeSalary.isChecked()) {
            log.debug("The person earns a full-time salary");
        } else {
            log.debug("The person does not earn a full-time salary");
        }*/
    }

    public void loadFields() {
        if (this.pdfDocument != null) {
            PDDocumentCatalog catalog = pdfDocument.getDocumentCatalog();
            PDAcroForm form = catalog.getAcroForm();
            this.fields = form.getFields();
        }
    }

    public List<String> getFieldNames() {
        List<String> fieldNames = this.fields.stream().map(PDField::getFullyQualifiedName).collect(Collectors.toList());

        return fieldNames;
    }

    public List<String> getFieldValues() {
        List<String> fieldValues = new ArrayList<>();

        for (PDField fieldValue : this.fields) {
            if (StringUtils.containsNone(fieldValue.getValueAsString(), "þÿ")) {
                fieldValues.add(fieldValue.getValueAsString());
            }
        }
        return fieldValues;
    }

    public void printFieldsAndValues() {
        if (this.pdfDocument != null) {
            PDDocumentCatalog catalog = pdfDocument.getDocumentCatalog();
            PDAcroForm form = catalog.getAcroForm();
            List<PDField> fields = form.getFields();

            System.out.println(String.format("found %s fields in document: %s%n", fields.size(), pdfFile.getName()));

//            fields.stream().forEach(pdField -> System.out.println(String.format("%s: %s", pdField.getFullyQualifiedName(), StringUtils.defaultIfBlank(pdField.getValueAsString(), "-"))));

            for (PDField field : fields) {
                String value = field.getValueAsString();
                String name = field.getFullyQualifiedName();
                System.out.println(String.format("%s: %s", name, StringUtils.defaultIfBlank(value, "-")));
            }
        }
    }

    public void close() {
        if (pdfDocument != null) {
            try {
                pdfDocument.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}
