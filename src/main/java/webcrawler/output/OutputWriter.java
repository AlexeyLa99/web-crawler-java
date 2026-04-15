package webcrawler.output;

import java.io.IOException;
import webcrawler.result.CrawlResult;

/**
 * Contract for writing crawl results to an output destination.
 */
public interface OutputWriter {

    /**
     * Writes the provided crawl result to the given file path.
     *
     * @param result the crawl result to serialize
     * @param filePath output file path
     * @throws IOException if writing fails
     */
    void write(CrawlResult result, String filePath) throws IOException;
}
