package webcrawler.analysis;

import java.util.List;
import webcrawler.result.PageData;

/**
 * Collects URLs of pages whose HTTP status is not 200, in discovery order.
 *
 * @author Alexey Laikov
 */
public class BrokenLinksAnalysis extends AbstractAnalysis {

    @Override
    protected Object doAnalyze(List<PageData> pages) {
        return pages.stream()
                .filter(p -> p.getStatus() != 200)
                .map(PageData::getUrl)
                .toList();
    }
}
