package webcrawler.crawler;

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
        // TODO (Talia): implement crawl logic here
    }
}
