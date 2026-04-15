package webcrawler.filter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * Domain filtering utility for crawl URL allowance checks.
 */
public final class DomainFilter {

    private DomainFilter() {
    }

    /**
     * Checks whether a URL is allowed by the provided domain set.
     *
     * @param url URL to evaluate
     * @param allowedDomains allowed domains; when null/empty all domains are allowed
     * @return true if URL is allowed
     */
    public static boolean isAllowed(String url, Set<String> allowedDomains) {
        if (allowedDomains == null || allowedDomains.isEmpty()) {
            return true;
        }

        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (host == null || host.isBlank()) {
                return false;
            }
            return allowedDomains.contains(host.toLowerCase());
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
