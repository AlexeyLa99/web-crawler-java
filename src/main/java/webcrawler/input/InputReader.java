package webcrawler.input;

import java.io.IOException;
import java.util.List;

/**
 * Contract for loading seed URLs from an input source.
 */
public interface InputReader {

    /**
     * Reads and returns crawl seed URLs.
     *
     * @return list of seed URLs
     * @throws IOException if input reading fails
     */
    List<String> readSeeds() throws IOException;
}
