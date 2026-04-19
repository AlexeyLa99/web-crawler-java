package webcrawler.analysis;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import webcrawler.result.PageData;

/**
 * Finds the domain with the most outgoing links across all crawled pages.
 *
 * <p>Ties are broken lexicographically (smallest domain name wins).
 * Returns an empty string when no pages are available.
 *
 * @author Alexey Laikov
 */
public class MostLinkedDomainAnalysis extends AbstractAnalysis {

    @Override
    protected Object doAnalyze(List<PageData> pages) {
        return pages.stream()
                .filter(p -> p.getDomain() != null && !p.getDomain().isBlank())
                .collect(Collectors.groupingBy(
                        PageData::getDomain,
                        Collectors.summingInt(PageData::getOutgoingLinks)))
                .entrySet().stream()
                .max(Comparator.<Map.Entry<String, Integer>, Integer>comparing(Map.Entry::getValue)
                        .thenComparing(Comparator.<Map.Entry<String, Integer>, String>comparing(Map.Entry::getKey).reversed()))
                .map(Map.Entry::getKey)
                .orElse("");
    }
}
