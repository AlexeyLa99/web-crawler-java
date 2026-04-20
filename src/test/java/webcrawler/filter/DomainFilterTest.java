package webcrawler.filter;

import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link DomainFilter}.
 */
class DomainFilterTest {

    @Test
    void emptyWhitelist_allowsAll() {
        assertTrue(DomainFilter.isAllowed("https://example.com/page", Collections.emptySet()));
        assertTrue(DomainFilter.isAllowed("https://anything.org", null));
    }

    @Test
    void urlInWhitelist_returnsTrue() {
        Set<String> allowed = Set.of("example.com");
        assertTrue(DomainFilter.isAllowed("https://example.com/page", allowed));
    }

    @Test
    void urlNotInWhitelist_returnsFalse() {
        Set<String> allowed = Set.of("example.com");
        assertFalse(DomainFilter.isAllowed("https://evil.com/page", allowed));
    }

    @Test
    void malformedUrl_returnsFalse() {
        Set<String> allowed = Set.of("example.com");
        assertFalse(DomainFilter.isAllowed("not a url", allowed));
    }
}
