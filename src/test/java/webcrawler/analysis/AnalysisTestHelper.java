package webcrawler.analysis;

import java.util.Collections;
import java.util.List;
import webcrawler.result.PageData;

/**
 * Small helper for building {@link PageData} mocks used across analysis tests.
 * Package-private: for test use only.
 */
final class AnalysisTestHelper {

    private AnalysisTestHelper() {
    }

    /** Successful page (status 200) with a word count and a body. */
    static PageData successPage(String url, String domain, int wordCount, String body) {
        return new PageData(url, "t", body, wordCount, Collections.emptyList(), domain, 200, 0);
    }

    /** Page with a custom HTTP status (e.g. 404, 500). */
    static PageData statusPage(String url, String domain, int status) {
        return new PageData(url, null, "", 0, Collections.emptyList(), domain, status, 0);
    }

    /** Successful page with a given list of outgoing links. */
    static PageData pageWithLinks(String url, String domain, List<String> outgoing) {
        return new PageData(url, "t", "", 0, outgoing, domain, 200, 0);
    }
}
