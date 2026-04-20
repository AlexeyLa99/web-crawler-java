package webcrawler.result;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link PageData}.
 */
class PageDataTest {

    @Test
    void gettersReturnConstructorValues() {
        List<String> links = List.of("https://a.com", "https://b.com");
        PageData page = new PageData(
                "https://example.com",
                "Example Title",
                "hello world",
                2,
                links,
                "example.com",
                200,
                1);

        assertEquals("https://example.com", page.getUrl());
        assertEquals("Example Title", page.getTitle());
        assertEquals("hello world", page.getBodyText());
        assertEquals(2, page.getWordCount());
        assertEquals(2, page.getOutgoingLinks());
        assertEquals(links, page.getOutgoingLinkUrls());
        assertEquals("example.com", page.getDomain());
        assertEquals(200, page.getStatus());
        assertEquals(1, page.getDepth());
    }

    @Test
    void outgoingLinkUrls_isUnmodifiable() {
        List<String> source = new ArrayList<>();
        source.add("https://a.com");
        PageData page = new PageData("u", null, "", 0, source, "d", 200, 0);

        assertThrows(
                UnsupportedOperationException.class,
                () -> page.getOutgoingLinkUrls().add("https://c.com"));
    }
}
