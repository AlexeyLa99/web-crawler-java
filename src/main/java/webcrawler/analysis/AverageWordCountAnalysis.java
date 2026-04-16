package webcrawler.analysis;

import java.util.List;
import webcrawler.result.PageData;

/**
 * Computes the average word count across all successfully fetched pages (HTTP 200).
 *
 * <p>Returns {@code 0.0} when no successful pages are present.
 *
 * @author Alexey Laikov
 */
public class AverageWordCountAnalysis extends AbstractAnalysis {

    @Override
    protected Object doAnalyze(List<PageData> pages) {
        return pages.stream()
                .filter(p -> p.getStatus() == 200)
                .mapToInt(PageData::getWordCount)
                .average()
                .orElse(0.0);
    }
}
