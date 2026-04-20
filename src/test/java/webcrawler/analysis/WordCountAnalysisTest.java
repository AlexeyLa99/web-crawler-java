package webcrawler.analysis;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import webcrawler.result.PageData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static webcrawler.analysis.AnalysisTestHelper.statusPage;
import static webcrawler.analysis.AnalysisTestHelper.successPage;

/**
 * Unit tests for {@link WordCountAnalysis}.
 */
class WordCountAnalysisTest {

    @Test
    void sumsWordCountsOfSuccessfulPages() {
        List<PageData> pages = List.of(
                successPage("https://a.com", "a.com", 10, ""),
                successPage("https://b.com", "b.com", 20, ""),
                successPage("https://c.com", "c.com", 30, ""));

        Object result = new WordCountAnalysis().analyze(pages);

        assertEquals(60, result);
    }

    @Test
    void ignoresNon200Pages() {
        List<PageData> pages = List.of(
                successPage("https://ok.com", "ok.com", 10, ""),
                // a "broken" page with a non-zero wordCount must still be excluded
                new PageData("https://fail.com", null, "", 50, Collections.emptyList(), "fail.com", 404, 0));

        Object result = new WordCountAnalysis().analyze(pages);

        assertEquals(10, result);
    }

    @Test
    void emptyList_returnsZero() {
        Object result = new WordCountAnalysis().analyze(Collections.emptyList());

        assertEquals(0, result);
    }
}
