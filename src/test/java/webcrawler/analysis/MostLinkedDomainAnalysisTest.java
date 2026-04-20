package webcrawler.analysis;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import webcrawler.result.PageData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static webcrawler.analysis.AnalysisTestHelper.pageWithLinks;

/**
 * Unit tests for {@link MostLinkedDomainAnalysis}.
 */
class MostLinkedDomainAnalysisTest {

    @Test
    void returnsDomainWithMostOutgoingLinks() {
        List<PageData> pages = List.of(
                pageWithLinks("https://a.com/1", "a.com", links(10)),
                pageWithLinks("https://b.com/1", "b.com", links(3)));

        Object result = new MostLinkedDomainAnalysis().analyze(pages);

        assertEquals("a.com", result);
    }

    @Test
    void tieBreakerPicksLexicographicallySmallestDomain() {
        List<PageData> pages = List.of(
                pageWithLinks("https://b.com/1", "b.com", links(5)),
                pageWithLinks("https://a.com/1", "a.com", links(5)));

        Object result = new MostLinkedDomainAnalysis().analyze(pages);

        assertEquals("a.com", result);
    }

    @Test
    void emptyList_returnsEmptyString() {
        Object result = new MostLinkedDomainAnalysis().analyze(Collections.emptyList());

        assertEquals("", result);
    }

    private static List<String> links(int count) {
        String[] arr = new String[count];
        for (int i = 0; i < count; i++) {
            arr[i] = "https://target-" + i + ".com";
        }
        return List.of(arr);
    }
}
