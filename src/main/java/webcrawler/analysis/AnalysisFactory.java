package webcrawler.analysis;

/**
 * Creates {@link AnalysisStrategy} instances by strategy name.
 *
 * @author Alexey Laikov
 */
public final class AnalysisFactory {

    private AnalysisFactory() {
    }

    /**
     * Returns the strategy matching {@code name}, or {@code null} if unknown.
     *
     * @param name strategy name as passed on the command line (e.g. {@code "WORD_COUNT"})
     * @return a new strategy instance, or {@code null}
     */
    public static AnalysisStrategy createStrategy(String name) {
        if (name == null) {
            return null;
        }
        return switch (name.trim().toUpperCase()) {
            case "WORD_COUNT"    -> new WordCountAnalysis();
            case "BROKEN_LINKS" -> new BrokenLinksAnalysis();
            default             -> null;
        };
    }
}
