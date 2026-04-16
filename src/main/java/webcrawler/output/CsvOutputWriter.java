package webcrawler.output;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import webcrawler.result.CrawlResult;
import webcrawler.result.PageData;

/**
 * Writes crawl output as a CSV file.
 *
 * <p>The file contains a header row followed by one row per crawled page.
 * Analysis results are appended as extra rows at the end, prefixed with {@code #}.
 *
 * @author Alexey Laikov
 */
public class CsvOutputWriter implements OutputWriter {

    private static final String HEADER =
            "url,title,status,depth,domain,wordCount,outgoingLinks";

    @Override
    public void write(CrawlResult result, String filePath) throws IOException {
        try (PrintWriter pw = new PrintWriter(
                Path.of(filePath).toFile(), StandardCharsets.UTF_8)) {

            pw.println(HEADER);
            for (PageData page : result.getPages()) {
                pw.println(toCsvRow(page));
            }

            for (Map.Entry<String, Object> entry : result.getAnalysis().entrySet()) {
                pw.println("# " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    private String toCsvRow(PageData page) {
        return escape(page.getUrl())           + "," +
               escape(page.getTitle())         + "," +
               page.getStatus()                + "," +
               page.getDepth()                 + "," +
               escape(page.getDomain())        + "," +
               page.getWordCount()             + "," +
               page.getOutgoingLinks();
    }

    /** Wraps a value in double quotes and escapes any embedded double quotes. */
    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
