package webcrawler.cli;

import java.io.File;
import java.util.*;

/**
 * Parses the command-line arguments and produces a {@link CrawlConfig}.
 *
 * <p>Expected invocation format:
 * <pre>
 *   java Main --analysis WORD_COUNT,BROKEN_LINKS \
 *             --poolsize 3 --depth 2             \
 *             --input seeds.txt --output report.json
 * </pre>
 *
 * <p>Optional flags:
 * <ul>
 *   <li>{@code --format}  – output format, default {@code "json"}</li>
 *   <li>{@code --domains} – comma-separated hostname whitelist</li>
 * </ul>
 *
 * <p>On any validation error the method prints a message to {@code System.err}
 * and calls {@code System.exit(1)}.
 *
 * @author Alexey Laikov
 * @author Talia Barzilai
 */
public class CliParser {

    /**
     * The set of analysis strategy names that this program recognises.
     * Any name not in this set is rejected with "{@code <name> is unknown}".
     */
    private static final Set<String> KNOWN_ANALYSES = Set.of(
        "WORD_COUNT",
        "MOST_LINKED_DOMAIN",
        "BROKEN_LINKS",
        "KEYWORD_FREQUENCY",
        "AVERAGE_WORD_COUNT"
    );

    /**
     * Parses {@code args} and returns the corresponding {@link CrawlConfig}.
     *
     * <p>Arguments are consumed in consecutive flag–value pairs:
     * {@code args[i]} is the flag (e.g. {@code "--poolsize"}) and
     * {@code args[i+1]} is its value (e.g. {@code "3"}).
     *
     * @param args the command-line argument array from {@code main}
     * @return a fully populated {@link CrawlConfig}
     */
    public static CrawlConfig parse(String[] args) {
        CrawlConfig.Builder builder = new CrawlConfig.Builder();

        for (int i = 0; i < args.length - 1; i += 2) {
            String flag  = args[i];
            String value = args[i + 1];

            switch (flag) {
                case "--analysis" -> {
                    String[] parts = value.split(",");
                    List<String> valid = new ArrayList<>();
                    for (String name : parts) {
                        String trimmed = name.trim();
                        if (KNOWN_ANALYSES.contains(trimmed)) {
                            valid.add(trimmed);
                        } else {
                            System.err.println(trimmed + " is unknown");
                        }
                    }
                    if (valid.isEmpty()) {
                        System.err.println("no valid analysis");
                        System.exit(1);
                    }
                    builder.analyses(valid);
                }
                case "--poolsize" -> {
                    int n;
                    try {
                        n = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        System.err.println("invalid pool size");
                        System.exit(1);
                    }
                    if (n <= 0) {
                        System.err.println("invalid pool size");
                        System.exit(1);
                    }
                    builder.poolSize(n);
                }
                case "--depth" -> {
                    int d;
                    try {
                        d = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        System.err.println("invalid depth");
                        System.exit(1);
                    }
                    if (d < 0) {
                        System.err.println("invalid depth");
                        System.exit(1);
                    }
                    builder.maxDepth(d);
                }
                case "--input" -> {
                    if (!"-".equals(value) && !new File(value).exists()) {
                        System.err.println("invalid input file");
                        System.exit(1);
                    }
                    builder.inputFile(value);
                }
                case "--output"  -> builder.outputFile(value);
                case "--format"  -> builder.format(value);
                case "--domains" -> {
                    Set<String> domains = new HashSet<>();
                    for (String d : value.split(",")) {
                        String trimmed = d.trim();
                        if (!trimmed.isEmpty()) {
                            domains.add(trimmed);
                        }
                    }
                    builder.allowedDomains(domains);
                }
            }
        }

        return builder.build();
    }
}
