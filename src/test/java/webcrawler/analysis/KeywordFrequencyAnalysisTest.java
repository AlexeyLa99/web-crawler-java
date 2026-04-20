package webcrawler.analysis;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import webcrawler.result.PageData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static webcrawler.analysis.AnalysisTestHelper.statusPage;
import static webcrawler.analysis.AnalysisTestHelper.successPage;

/**
 * Unit tests for {@link KeywordFrequencyAnalysis}.
 */
class KeywordFrequencyAnalysisTest {

    @Test
    void countsAllThreeKeywords() {
        List<PageData> pages = List.of(
                successPage("https://a.com", "a.com", 0, "Java is great. Thread pattern in Java."));

        @SuppressWarnings("unchecked")
        Map<String, Integer> result = (Map<String, Integer>) new KeywordFrequencyAnalysis().analyze(pages);

        assertEquals(2, result.get("java"));
        assertEquals(1, result.get("thread"));
        assertEquals(1, result.get("pattern"));
    }

    @Test
    void caseInsensitiveWholeWord() {
        // "javascript", "threads", "patterns" must NOT match (\b boundary).
        List<PageData> pages = List.of(
                successPage("https://a.com", "a.com", 0,
                        "JAVA javascript thread threads pattern patterns"));

        @SuppressWarnings("unchecked")
        Map<String, Integer> result = (Map<String, Integer>) new KeywordFrequencyAnalysis().analyze(pages);

        assertEquals(1, result.get("java"));
        assertEquals(1, result.get("thread"));
        assertEquals(1, result.get("pattern"));
    }

    @Test
    void ignoresNon200Pages() {
        List<PageData> pages = List.of(
                successPage("https://ok.com", "ok.com", 0, "java thread pattern"),
                // "broken" page body must be ignored even if it contains keywords
                new PageData("https://fail.com", null, "java java java",
                        0, List.of(), "fail.com", 404, 0));

        @SuppressWarnings("unchecked")
        Map<String, Integer> result = (Map<String, Integer>) new KeywordFrequencyAnalysis().analyze(pages);

        assertEquals(1, result.get("java"));
        assertEquals(1, result.get("thread"));
        assertEquals(1, result.get("pattern"));
    }
}
