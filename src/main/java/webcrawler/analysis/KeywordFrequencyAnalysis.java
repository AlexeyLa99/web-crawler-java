package webcrawler.analysis;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import webcrawler.result.PageData;

/**
 * Counts occurrences of the keywords {@code java}, {@code thread}, and {@code pattern}
 * (case-insensitive, whole-word) across the body text of all successfully fetched pages.
 *
 * <p>The result is a {@code Map<String, Integer>} with one entry per keyword,
 * preserved in the order: java → thread → pattern.
 *
 * @author Alexey Laikov
 */
public class KeywordFrequencyAnalysis extends AbstractAnalysis {

    private static final List<String> KEYWORDS = List.of("java", "thread", "pattern");

    private static final Map<String, Pattern> PATTERNS;

    static {
        PATTERNS = new LinkedHashMap<>();
        for (String kw : KEYWORDS) {
            PATTERNS.put(kw, Pattern.compile("\\b" + kw + "\\b", Pattern.CASE_INSENSITIVE));
        }
    }

    @Override
    protected Object doAnalyze(List<PageData> pages) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String kw : KEYWORDS) {
            counts.put(kw, 0);
        }

        for (PageData page : pages) {
            if (page.getStatus() != 200) {
                continue;
            }
            String text = page.getBodyText();
            for (Map.Entry<String, Pattern> entry : PATTERNS.entrySet()) {
                Matcher m = entry.getValue().matcher(text);
                int found = 0;
                while (m.find()) {
                    found++;
                }
                counts.merge(entry.getKey(), found, Integer::sum);
            }
        }

        return counts;
    }
}
