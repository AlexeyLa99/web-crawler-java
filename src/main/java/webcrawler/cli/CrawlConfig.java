package webcrawler.cli;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Immutable configuration object for a single crawl session.
 *
 * <p>Use {@link Builder} to construct instances:
 * <pre>
 *   CrawlConfig cfg = new CrawlConfig.Builder()
 *       .analyses(List.of("WORD_COUNT"))
 *       .poolSize(4)
 *       .maxDepth(2)
 *       .inputFile("seeds.txt")
 *       .outputFile("report.json")
 *       .build();
 * </pre>
 *
 * @author Alexey Laikov
 * @author Talia Barzilai
 */
public class CrawlConfig {

    private final List<String> analyses;
    private final int poolSize;
    private final int maxDepth;
    private final String inputFile;
    private final String outputFile;
    private final String format;
    private final Set<String> allowedDomains;

    /** Only {@link Builder#build()} may instantiate this class. */
    private CrawlConfig(Builder b) {
        this.analyses       = b.analyses;
        this.poolSize       = b.poolSize;
        this.maxDepth       = b.maxDepth;
        this.inputFile      = b.inputFile;
        this.outputFile     = b.outputFile;
        this.format         = b.format;
        this.allowedDomains = b.allowedDomains;
    }

    /** @return names of the requested analysis strategies */
    public List<String> getAnalyses()      { return analyses; }

    /** @return number of worker threads in the pool */
    public int getPoolSize()               { return poolSize; }

    /** @return maximum crawl depth (0 = seed pages only) */
    public int getMaxDepth()               { return maxDepth; }

    /** @return path to the seed-URL input file */
    public String getInputFile()           { return inputFile; }

    /** @return path where the output report will be written */
    public String getOutputFile()          { return outputFile; }

    /** @return output format identifier, e.g. {@code "json"} or {@code "csv"} */
    public String getFormat()              { return format; }

    /** @return hostname whitelist; empty set means all domains are allowed */
    public Set<String> getAllowedDomains() { return allowedDomains; }

    // -------------------------------------------------------------------------

    /**
     * Fluent builder for {@link CrawlConfig}.
     *
     * <p>All setters return {@code this} to allow method chaining.
     * Call {@link #build()} at the end to obtain the immutable config object.
     */
    public static class Builder {

        private List<String> analyses;
        private int poolSize;
        private int maxDepth;
        private String inputFile;
        private String outputFile;
        private String format         = "json";        // default output format
        private Set<String> allowedDomains = new HashSet<>(); // empty = allow all

        /** Sets the list of requested analysis strategy names. */
        public Builder analyses(List<String> v)       { analyses = v;       return this; }

        /** Sets the thread-pool size (must be &gt; 0). */
        public Builder poolSize(int v)                { poolSize = v;       return this; }

        /** Sets the maximum crawl depth (must be &ge; 0). */
        public Builder maxDepth(int v)                { maxDepth = v;       return this; }

        /** Sets the path to the seed-URL input file. */
        public Builder inputFile(String v)            { inputFile = v;      return this; }

        /** Sets the path for the output report file. */
        public Builder outputFile(String v)           { outputFile = v;     return this; }

        /** Sets the output format (e.g. {@code "json"}, {@code "csv"}). */
        public Builder format(String v)               { format = v;         return this; }

        /** Sets the hostname whitelist (empty set = allow all). */
        public Builder allowedDomains(Set<String> v)  { allowedDomains = v; return this; }

        /**
         * Constructs the {@link CrawlConfig} from the current builder state.
         *
         * @return a new immutable {@code CrawlConfig}
         */
        public CrawlConfig build() {
            return new CrawlConfig(this);
        }
    }
}
