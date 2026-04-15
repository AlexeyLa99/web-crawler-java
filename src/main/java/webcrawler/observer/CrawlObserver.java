package webcrawler.observer;

import webcrawler.result.PageData;

/**
 * Observer notified whenever a page is crawled.
 */
public interface CrawlObserver {

    /**
     * Called after a page has been crawled and collected.
     *
     * @param pageData crawled page data
     */
    void onPageCrawled(PageData pageData);
}
