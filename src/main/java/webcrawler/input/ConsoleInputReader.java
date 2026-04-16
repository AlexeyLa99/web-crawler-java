package webcrawler.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Reads crawl seed URLs from standard input (one URL per line).
 */
public class ConsoleInputReader implements InputReader {

    @Override
    public List<String> readSeeds() throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            return reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .filter(line -> !line.startsWith("#"))
                    .toList();
        }
    }
}
