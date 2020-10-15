package export;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import static utils.Constants.DEFAULT_SEPARATOR;

/**
 * Created by samuelstein on 15.11.2016.
 * <p>
 * Inspirations came from https://www.mkyong.com/java/how-to-export-data-to-csv-file-java/
 */
public class CsvExporter {

    public CsvExporter() {
    }

    public boolean writeFile(Writer w, List<String> headlines, List<String> content, String outputFile) {
        try {
            writeLine(w, headlines);
            writeLine(w, content);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void testCsvExport(Writer writer) throws IOException {
        writeLine(writer, Arrays.asList("a", "b", "c", "d"));
        //custom separator + quote
        writeLine(writer, Arrays.asList("aaa", "bb,b", "cc,c"), ',', '"');
        //custom separator + quote
        writeLine(writer, Arrays.asList("aaa", "bbb", "cc,c"), '|', '\'');
        //double-quotes
        writeLine(writer, Arrays.asList("aaa", "bbb", "cc\"c"));
    }

    public void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR, ' ');
    }

    public void writeLine(Writer w, List<String> values, char separators) throws IOException {
        writeLine(w, values, separators, ' ');
    }

    //see https://tools.ietf.org/html/rfc4180
    private String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;
    }

    public void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {
        boolean first = true;

        //default customQuote is empty
        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());
    }
}
