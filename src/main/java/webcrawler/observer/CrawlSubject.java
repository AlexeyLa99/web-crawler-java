package webcrawler.observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import webcrawler.result.PageData;

/**
 * Subject that manages crawl observers and notifications.
 */
public class CrawlSubject {

    private final List<CrawlObserver> observers = new CopyOnWriteArrayList<>();

    /**
     * Adds an observer to receive crawl events.
     *
     * @param observer observer instance
     */
    public void addObserver(CrawlObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    /**
     * Notifies all registered observers about a crawled page.
     *
     * @param pageData crawled page data
     */
    public void notifyObservers(PageData pageData) {
        for (CrawlObserver observer : observers) {
            observer.onPageCrawled(pageData);
        }
    }
}
