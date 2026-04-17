package webcrawler.crawler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import webcrawler.filter.DomainFilter;
import webcrawler.result.PageData;

/**
 * A single crawl unit – fetches one URL and schedules its outgoing links.
 *
 * <p>Lifecycle per task:
 * <ol>
 *   <li>Atomically claim the URL via {@code visited.putIfAbsent} – if already seen, return.</li>
 *   <li>Fetch the page with {@link PageFetcher#fetch}.</li>
 *   <li>Add the resulting {@link PageData} to {@code crawler.results}.</li>
 *   <li>If depth &lt; maxDepth, submit child tasks for outgoing links.</li>
 *   <li>In {@code finally}: decrement {@code pendingTasks}; if 0 → release {@code doneLatch}.</li>
 *   <li>Notify observers via {@code crawler.subject}.</li>
 * </ol>
 */
public class CrawlTask implements Runnable {

    private static final int FETCH_TIMEOUT_MS = 3000;

    private final String url;
    private final int depth;
    private final WebCrawler crawler;

    /**
     * @param url     absolute URL to crawl
     * @param depth   depth at which this URL was discovered (root seeds = 0)
     * @param crawler the owning {@link WebCrawler} – provides shared state and {@code submit()}
     */
    public CrawlTask(String url, int depth, WebCrawler crawler) {
        this.url = url;
        this.depth = depth;
        this.crawler = crawler;
    }

    @Override
    public void run() {
        try {
            int order = crawler.orderCounter.getAndIncrement();
            Integer existing = crawler.visited.putIfAbsent(url, order);
            if (existing != null) {
                return;
            }

            PageData pageData = PageFetcher.fetch(url, depth);
            crawler.results.add(pageData);

            if (depth < crawler.getConfig().getMaxDepth()) {
                Set<String> allowedDomains = crawler.getConfig().getAllowedDomains();
                for (String childUrl : extractOutgoingLinks(url)) {
                    if (DomainFilter.isAllowed(childUrl, allowedDomains)) {
                        crawler.submit(childUrl, depth + 1);
                    }
                }
            }

            crawler.getSubject().notifyObservers(pageData);
        } finally {
            if (crawler.pendingTasks.decrementAndGet() == 0) {
                crawler.doneLatch.countDown();
            }
        }
    }

    private Set<String> extractOutgoingLinks(String sourceUrl) {
        Set<String> outgoing = new HashSet<>();
        try {
            Connection.Response response = Jsoup.connect(sourceUrl)
                    .timeout(FETCH_TIMEOUT_MS)
                    .followRedirects(true)
                    .ignoreHttpErrors(true)
                    .execute();

            if (response.statusCode() != 200) {
                return outgoing;
            }

            Document doc = response.parse();
            Elements anchors = doc.select("a[href]");
            for (Element anchor : anchors) {
                String absolute = anchor.attr("abs:href");
                if (absolute == null || absolute.isBlank()) {
                    continue;
                }
                try {
                    URI uri = new URI(absolute.trim());
                    String scheme = uri.getScheme();
                    if (uri.isAbsolute() && scheme != null
                            && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
                        outgoing.add(uri.toString());
                    }
                } catch (URISyntaxException ignored) {
                    // Skip malformed outgoing links.
                }
            }
        } catch (IOException | IllegalArgumentException ignored) {
            // Keep crawler resilient; PageFetcher already reports fetch failures.
        }
        return outgoing;
    }
}
