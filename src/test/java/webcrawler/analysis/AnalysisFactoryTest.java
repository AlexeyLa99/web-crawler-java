package webcrawler.analysis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link AnalysisFactory}.
 */
class AnalysisFactoryTest {

    @Test
    void knownNames_returnCorrectStrategies() {
        assertInstanceOf(WordCountAnalysis.class,        AnalysisFactory.createStrategy("WORD_COUNT"));
        assertInstanceOf(BrokenLinksAnalysis.class,      AnalysisFactory.createStrategy("BROKEN_LINKS"));
        assertInstanceOf(MostLinkedDomainAnalysis.class, AnalysisFactory.createStrategy("MOST_LINKED_DOMAIN"));
        assertInstanceOf(KeywordFrequencyAnalysis.class, AnalysisFactory.createStrategy("KEYWORD_FREQUENCY"));
        assertInstanceOf(AverageWordCountAnalysis.class, AnalysisFactory.createStrategy("AVERAGE_WORD_COUNT"));
    }

    @Test
    void caseInsensitiveAndTrimmed() {
        assertInstanceOf(WordCountAnalysis.class, AnalysisFactory.createStrategy("word_count"));
        assertInstanceOf(WordCountAnalysis.class, AnalysisFactory.createStrategy("  WORD_COUNT  "));
    }

    @Test
    void unknownOrNullName_returnsNull() {
        assertNull(AnalysisFactory.createStrategy("FOO"));
        assertNull(AnalysisFactory.createStrategy(""));
        assertNull(AnalysisFactory.createStrategy(null));
    }
}
