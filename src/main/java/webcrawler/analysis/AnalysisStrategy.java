package webcrawler.analysis;

import java.util.List;
import webcrawler.result.PageData;

/**
 * Contract for a single analysis that can be run over a completed crawl.
 *
 * @author Alexey Laikov
 */
public interface AnalysisStrategy {

    /**
     * Runs the analysis over the given list of crawled pages.
     *
     * @param pages all pages collected during the crawl
     * @return the analysis result; concrete type depends on the implementation
     */
    Object analyze(List<PageData> pages);
}
