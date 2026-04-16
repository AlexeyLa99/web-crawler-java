package webcrawler.crawler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import webcrawler.result.PageData;

/**
 * Fetches a single page and builds {@link PageData} using Jsoup.
 */
public final class PageFetcher {

    private static final int TIMEOUT_MS = 3000;

    private PageFetcher() {
    }

    /**
     * Fetches {@code url} at the given crawl depth and returns structured page data.
     *
     * <p>On malformed URLs, prints {@code <url> malformed} to stderr and returns a placeholder
     * {@link PageData} with status 0. On I/O errors (including timeouts), prints
     * {@code <url> failed} to stderr and returns a placeholder with status 0.</p>
     *
     * @param url   absolute HTTP/HTTPS URL
     * @param depth crawl depth for this page
     * @return never {@code null}
     */
    public static PageData fetch(String url, int depth) {
        String trimmed = url == null ? "" : url.trim();
        if (trimmed.isEmpty()) {
            System.err.println(" malformed");
            return new PageData("", null, "", 0, 0, "", 0, depth);
        }

        URI uri;
        try {
            uri = new URI(trimmed);
        } catch (URISyntaxException e) {
            System.err.println(trimmed + " malformed");
            return new PageData(trimmed, null, "", 0, 0, "", 0, depth);
        }

        String scheme = uri.getScheme();
        if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
            System.err.println(trimmed + " malformed");
            return new PageData(trimmed, null, "", 0, 0, "", 0, depth);
        }

        String domain = hostOf(trimmed);
        if (domain.isEmpty()) {
            System.err.println(trimmed + " malformed");
            return new PageData(trimmed, null, "", 0, 0, "", 0, depth);
        }

        try {
            Connection.Response response = Jsoup.connect(trimmed)
                    .timeout(TIMEOUT_MS)
                    .followRedirects(true)
                    .ignoreHttpErrors(true)
                    .execute();

            int status = response.statusCode();
            Document doc = response.parse();

            if (status != 200) {
                return new PageData(trimmed, null, "", 0, 0, domain, status, depth);
            }

            String title = emptyToNull(doc.title());
            String bodyText = doc.body() != null ? doc.body().text() : "";
            int wordCount = countWords(bodyText);

            Set<String> outgoing = new HashSet<>();
            Elements anchors = doc.select("a[href]");
            for (Element a : anchors) {
                String abs = a.attr("abs:href");
                if (abs == null || abs.isBlank()) {
                    continue;
                }
                try {
                    URI linkUri = new URI(abs.trim());
                    if (!linkUri.isAbsolute()) {
                        continue;
                    }
                    String linkScheme = linkUri.getScheme();
                    if (linkScheme != null
                            && (linkScheme.equalsIgnoreCase("http") || linkScheme.equalsIgnoreCase("https"))) {
                        outgoing.add(linkUri.toString());
                    }
                } catch (URISyntaxException ignored) {
                    // skip invalid links
                }
            }

            return new PageData(
                    trimmed,
                    title,
                    bodyText,
                    wordCount,
                    outgoing.size(),
                    domain,
                    status,
                    depth);
        } catch (IllegalArgumentException e) {
            System.err.println(trimmed + " malformed");
            return new PageData(trimmed, null, "", 0, 0, domain, 0, depth);
        } catch (IOException e) {
            System.err.println(trimmed + " failed");
            return new PageData(trimmed, null, "", 0, 0, domain, 0, depth);
        }
    }

    private static String hostOf(String url) {
        try {
            URI u = new URI(url);
            String host = u.getHost();
            return host == null ? "" : host.toLowerCase(Locale.ROOT);
        } catch (URISyntaxException e) {
            return "";
        }
    }

    private static String emptyToNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s;
    }

    private static int countWords(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }
}
