package webcrawler.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import webcrawler.cli.CrawlConfig;
import webcrawler.filter.DomainFilter;
import webcrawler.input.InputReader;
import webcrawler.observer.CrawlSubject;
import webcrawler.result.PageData;

/**
 * Concurrent BFS web crawler.
 *
 * <p>Spawns a fixed thread pool of size {@link CrawlConfig#getPoolSize()} and submits
 * {@link CrawlTask} instances. Crawling ends when every in-flight task has finished
 * (tracked by {@link #pendingTasks}) and {@link #doneLatch} is released.</p>
 */
public class WebCrawler {

    final ExecutorService pool;

    /**
     * Maps each visited URL to its discovery order number.
     * {@code putIfAbsent} is atomic – prevents two threads from crawling the same URL.
     */
    final ConcurrentHashMap<String, Integer> visited = new ConcurrentHashMap<>();

    /** Monotonically increasing counter – each new URL gets the next integer. */
    final AtomicInteger orderCounter = new AtomicInteger(0);

    /**
     * Counts tasks currently in flight (submitted but not yet finished).
     * Incremented BEFORE every {@code pool.submit()}, decremented in the
     * {@code finally} block of {@link CrawlTask#run()}.
     * When it reaches 0, the crawl is complete.
     */
    final AtomicInteger pendingTasks = new AtomicInteger(0);

    /**
     * Released (counted down to 0) by the last finishing {@link CrawlTask}
     * when {@link #pendingTasks} hits 0.
     */
    final CountDownLatch doneLatch = new CountDownLatch(1);

    /** Collects all crawled pages. Wrapped with {@link Collections#synchronizedList}. */
    final List<PageData> results = Collections.synchronizedList(new ArrayList<>());

    private final CrawlConfig config;
    private final InputReader inputReader;
    final CrawlSubject subject = new CrawlSubject();

    public WebCrawler(CrawlConfig config, InputReader inputReader) {
        this.config = config;
        this.inputReader = inputReader;
        this.pool = Executors.newFixedThreadPool(config.getPoolSize());
    }

    /**
     * Runs the crawl from seed URLs to {@link CrawlConfig#getMaxDepth()} depth.
     *
     * <ol>
     *   <li>Loads seed URLs via {@link InputReader#readSeeds()}.</li>
     *   <li>Submits one {@link CrawlTask} per seed (depth = 0).</li>
     *   <li>Waits on {@link #doneLatch} until all tasks finish.</li>
     *   <li>Shuts down the pool and returns pages sorted by discovery order.</li>
     * </ol>
     *
     * @return pages in discovery order (ascending order number)
     * @throws IOException          if the input reader fails
     * @throws InterruptedException if the waiting thread is interrupted
     */
    public List<PageData> crawl() throws IOException, InterruptedException {
        List<String> seeds = inputReader.readSeeds();

        for (String url : seeds) {
            Set<String> allowed = config.getAllowedDomains();
            if (!DomainFilter.isAllowed(url, allowed)) {
                continue;
            }
            submit(url, 0);
        }

        if (pendingTasks.get() == 0) {
            doneLatch.countDown();
        }

        doneLatch.await();
        pool.shutdown();

        synchronized (results) {
            results.sort(Comparator.comparingInt(p -> visited.getOrDefault(p.getUrl(), 0)));
        }
        return new ArrayList<>(results);
    }

    /**
     * Increments {@link #pendingTasks} and submits a new {@link CrawlTask} to the pool.
     * Must be called BEFORE {@code pool.submit} so the counter is never transiently 0.
     *
     * @param url   URL to crawl
     * @param depth depth at which this URL was discovered
     */
    void submit(String url, int depth) {
        pendingTasks.incrementAndGet();
        pool.submit(new CrawlTask(url, depth, this));
    }

    public CrawlConfig getConfig()   { return config; }
    public CrawlSubject getSubject() { return subject; }
}
