package webcrawler.input;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Reads seed URLs from a text file.
 */
public class FileInputReader implements InputReader {

    private final Path filePath;

    public FileInputReader(String filePath) {
        this.filePath = Path.of(filePath);
    }

    @Override
    public List<String> readSeeds() throws IOException {
        return Files.readAllLines(filePath).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .filter(line -> !line.startsWith("#"))
                .toList();
    }
}
