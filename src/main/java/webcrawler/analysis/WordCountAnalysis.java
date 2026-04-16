package webcrawler.analysis;

import java.util.List;
import webcrawler.result.PageData;

/**
 * Sums the word counts of all successfully fetched pages (HTTP 200).
 *
 * @author Alexey Laikov
 */
public class WordCountAnalysis extends AbstractAnalysis {

    @Override
    protected Object doAnalyze(List<PageData> pages) {
        return pages.stream()
                .filter(p -> p.getStatus() == 200)
                .mapToInt(PageData::getWordCount)
                .sum();
    }
}
