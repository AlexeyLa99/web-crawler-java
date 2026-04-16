package webcrawler.analysis;

import java.util.List;
import webcrawler.result.PageData;

/**
 * Base class for analysis strategies, using the Template Method pattern.
 *
 * <p>Subclasses implement {@link #doAnalyze(List)} with the actual logic;
 * this class owns the public {@link #analyze(List)} entry point.
 *
 * @author Alexey Laikov
 */
public abstract class AbstractAnalysis implements AnalysisStrategy {

    @Override
    public final Object analyze(List<PageData> pages) {
        return doAnalyze(pages);
    }

    /**
     * Performs the concrete analysis.
     *
     * @param pages all pages collected during the crawl
     * @return the analysis result
     */
    protected abstract Object doAnalyze(List<PageData> pages);
}
