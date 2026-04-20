package webcrawler.cli;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link CliParser}.
 *
 * <p>Only the happy path is covered here because error branches call
 * {@link System#exit(int)}, which cannot be exercised from inside a test.
 */
class CliParserTest {

    @Test
    void validArgs_buildsConfigCorrectly() {
        String[] args = {
                "--analysis", "WORD_COUNT,BROKEN_LINKS",
                "--poolsize", "4",
                "--depth",    "2",
                "--input",    "-",
                "--output",   "report.json",
                "--format",   "json",
                "--domains",  "example.com,foo.org"
        };

        CrawlConfig cfg = CliParser.parse(args);

        assertEquals(List.of("WORD_COUNT", "BROKEN_LINKS"), cfg.getAnalyses());
        assertEquals(4, cfg.getPoolSize());
        assertEquals(2, cfg.getMaxDepth());
        assertEquals("-", cfg.getInputFile());
        assertEquals("report.json", cfg.getOutputFile());
        assertEquals("json", cfg.getFormat());
        assertEquals(Set.of("example.com", "foo.org"), cfg.getAllowedDomains());
    }

    @Test
    void defaultFormatIsJson_whenNotProvided() {
        String[] args = {
                "--analysis", "WORD_COUNT",
                "--poolsize", "1",
                "--depth",    "0",
                "--input",    "-",
                "--output",   "out.json"
        };

        CrawlConfig cfg = CliParser.parse(args);

        assertEquals("json", cfg.getFormat());
    }
}
