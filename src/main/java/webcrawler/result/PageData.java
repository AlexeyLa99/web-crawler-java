package webcrawler.result;

/**
 * Holds all data collected from a single crawled web page.
 *
 * <p>Instances are immutable and are created by {@code PageFetcher}
 * after a successful (or failed) HTTP request.</p>
 *
 * @author Alexey Laikov
 * @author Talia Barzilai
 */
public class PageData {

    private final String url;
    private final String title;
    private final int wordCount;
    private final int outgoingLinks;
    private final String domain;
    private final int status;
    private final int depth;

    /**
     * Constructs a {@code PageData} record.
     *
     * @param url           the absolute URL of the page
     * @param title         the page {@code <title>}, or {@code null} when status != 200
     * @param wordCount     number of words in the visible body text (0 when status != 200)
     * @param outgoingLinks number of absolute {@code <a href>} links found (0 when status != 200)
     * @param domain        the hostname extracted from {@code url}
     * @param status        the HTTP response status code
     * @param depth         the crawl depth at which this page was discovered (root = 0)
     */
    public PageData(String url, String title, int wordCount,
                    int outgoingLinks, String domain, int status, int depth) {
        this.url = url;
        this.title = title;
        this.wordCount = wordCount;
        this.outgoingLinks = outgoingLinks;
        this.domain = domain;
        this.status = status;
        this.depth = depth;
    }

    /** @return the absolute URL of this page */
    public String getUrl()          { return url; }

    /** @return the page title, or {@code null} if the page was not successfully fetched */
    public String getTitle()        { return title; }

    /** @return the number of words in the visible body text */
    public int getWordCount()       { return wordCount; }

    /** @return the number of outgoing links found on this page */
    public int getOutgoingLinks()   { return outgoingLinks; }

    /** @return the hostname (domain) of this page's URL */
    public String getDomain()       { return domain; }

    /** @return the HTTP status code returned by the server */
    public int getStatus()          { return status; }

    /** @return the depth at which this page was discovered during crawling */
    public int getDepth()           { return depth; }
}
