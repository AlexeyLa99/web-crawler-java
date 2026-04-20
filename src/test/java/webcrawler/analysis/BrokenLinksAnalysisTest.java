package webcrawler.analysis;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import webcrawler.result.PageData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static webcrawler.analysis.AnalysisTestHelper.statusPage;
import static webcrawler.analysis.AnalysisTestHelper.successPage;

/**
 * Unit tests for {@link BrokenLinksAnalysis}.
 */
class BrokenLinksAnalysisTest {

    @Test
    void returnsOnlyNon200Urls() {
        List<PageData> pages = List.of(
                successPage("https://ok1.com", "ok1.com", 5, ""),
                statusPage("https://fail1.com", "fail1.com", 404),
                successPage("https://ok2.com", "ok2.com", 5, ""),
                statusPage("https://fail2.com", "fail2.com", 500));

        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) new BrokenLinksAnalysis().analyze(pages);

        assertEquals(List.of("https://fail1.com", "https://fail2.com"), result);
    }

    @Test
    void preservesDiscoveryOrder() {
        List<PageData> pages = List.of(
                statusPage("https://first.com", "first.com", 500),
                successPage("https://middle.com", "middle.com", 1, ""),
                statusPage("https://last.com", "last.com", 404));

        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) new BrokenLinksAnalysis().analyze(pages);

        assertEquals(List.of("https://first.com", "https://last.com"), result);
    }

    @Test
    void allSuccessful_returnsEmptyList() {
        List<PageData> pages = List.of(
                successPage("https://a.com", "a.com", 1, ""),
                successPage("https://b.com", "b.com", 1, ""));

        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) new BrokenLinksAnalysis().analyze(pages);

        assertEquals(Collections.emptyList(), result);
    }
}
