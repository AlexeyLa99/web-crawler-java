package webcrawler.result;

import java.util.List;
import java.util.Map;

/**
 * The aggregated result of a completed crawl session.
 *
 * <p>Contains the ordered list of every page that was visited together
 * with a map of named analysis results produced by the configured
 * {@code AnalysisStrategy} implementations.</p>
 *
 * @author Alexey Laikov
 * @author Talia Barzilai
 * @see PageData
 */
public class CrawlResult {

    private final List<PageData> pages;
    private final Map<String, Object> analysis;

    /**
     * Constructs a {@code CrawlResult}.
     *
     * @param pages    ordered list of all crawled pages (discovery order)
     * @param analysis map from analysis name to its computed value;
     *                 value type depends on the concrete {@code AnalysisStrategy}
     */
    public CrawlResult(List<PageData> pages, Map<String, Object> analysis) {
        this.pages = pages;
        this.analysis = analysis;
    }

    /** @return the ordered list of all pages visited during the crawl */
    public List<PageData> getPages()            { return pages; }

    /** @return a map from analysis strategy name to its computed result */
    public Map<String, Object> getAnalysis()    { return analysis; }
}
