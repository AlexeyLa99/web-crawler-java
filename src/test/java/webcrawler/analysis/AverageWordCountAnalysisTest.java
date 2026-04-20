package webcrawler.analysis;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import webcrawler.result.PageData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static webcrawler.analysis.AnalysisTestHelper.successPage;

/**
 * Unit tests for {@link AverageWordCountAnalysis}.
 */
class AverageWordCountAnalysisTest {

    @Test
    void averageOfSuccessfulPages() {
        List<PageData> pages = List.of(
                successPage("https://a.com", "a.com", 10, ""),
                successPage("https://b.com", "b.com", 20, ""),
                successPage("https://c.com", "c.com", 30, ""));

        Object result = new AverageWordCountAnalysis().analyze(pages);

        assertEquals(20.0, result);
    }

    @Test
    void emptyList_returnsZero() {
        Object result = new AverageWordCountAnalysis().analyze(Collections.emptyList());

        assertEquals(0.0, result);
    }
}
